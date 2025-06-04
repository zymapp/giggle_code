# 一、环境搭建

## 🧩 1、系统环境

✅ 系统环境
```
操作系统	Ubuntu Server 22.04 LTS 64位
CPU	8核
内存	32GB
GPU	1 * NVIDIA T4  
```  

## 🧩 2、whisper 部署步骤

1️⃣ 安装 NVIDIA 驱动 + CUDA 工具链
```
# 更新系统
sudo apt update && sudo apt upgrade

# 安装依赖
sudo apt install build-essential dkms

# 添加 CUDA 仓库并安装（以 CUDA 11.8 为例）
wget https://developer.download.nvidia.com/compute/cuda/repos/ubuntu2004/x86_64/cuda-ubuntu2004.pin
sudo mv cuda-ubuntu2004.pin /etc/apt/preferences.d/cuda-repository-pin-600
wget https://developer.download.nvidia.com/compute/cuda/11.8.0/local_installers/cuda-repo-ubuntu2004-11-8-local_11.8.0-1_amd64.deb
sudo dpkg -i cuda-repo-ubuntu2004-11-8-local_11.8.0-1_amd64.deb
sudo cp /var/cuda-repo-ubuntu2004-11-8-local/cuda-*-keyring.gpg /usr/share/keyrings/
sudo apt update
sudo apt install cuda

# 添加到环境变量
echo 'export PATH=/usr/local/cuda/bin:$PATH' >> ~/.bashrc
echo 'export LD_LIBRARY_PATH=/usr/local/cuda/lib64:$LD_LIBRARY_PATH' >> ~/.bashrc
source ~/.bashrc
```

2️⃣ 安装 Python 环境和依赖
```
# 安装 Python 和 venv
sudo apt install python3 python3-pip python3-venv

# 创建虚拟环境
python3 -m venv whisper-env
source whisper-env/bin/activate

# 升级 pip
pip install --upgrade pip
```

3️⃣ 安装 PyTorch（GPU 版本）
访问 https://pytorch.org/get-started/locally/，选择适合你 CUDA 版本的命令（如 CUDA 11.8）：
```
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118
```
验证是否使用 GPU：
```
# 在 python 中执行
import torch
print(torch.cuda.is_available())  # True 表示成功
```

4️⃣ 安装 Whisper 并测试 GPU 加速
```
pip install git+https://github.com/openai/whisper.git
```

5️⃣ 测试：
```
whisper example.mp3 --model medium --device cuda
```
如果运行时 GPU 被使用，会看到明显加速。

## 🧩 3、将whisper 部署为 API 服务（使用 FastAPI）

1️⃣ 安装uvicorn
```
pip install fastapi uvicorn
```

2️⃣ 创建 app.py：
```
from fastapi import FastAPI, UploadFile, File
import whisper
import tempfile

app = FastAPI()
model = whisper.load_model("medium", device="cuda")

@app.post("/transcribe/")
async def transcribe(file: UploadFile = File(...)):
    with tempfile.NamedTemporaryFile(delete=False) as tmp:
        tmp.write(await file.read())
        tmp_path = tmp.name
    result = model.transcribe(tmp_path)
    return {"text": result["text"]}
```

3️⃣ 启动 API：
```
uvicorn app:app --host 0.0.0.0 --port 8000
or
nohup python -m uvicorn app:app --host 0.0.0.0 --port 8000 > uvicorn.log 2>&1 &
```

4️⃣ 查看是否启动成功
ps aux | grep uvicorn


5️⃣ 测试whisper接口：
```
curl -X POST "http://localhost:8000/transcribe/" \
  -F "file=@/home/OK/1.mp3"
```
返回以下结果代表 whisper的环境成功
![image](https://github.com/user-attachments/assets/f02387e2-3235-45e0-8594-42bd705dbb2b)


文档地址在 8000/docs，如下：
http://119.45.168.124:8000/docs
<img width="1467" alt="image" src="https://github.com/user-attachments/assets/5b1b8a68-d41b-4f24-879e-f4dcb1e84da6" />


✅ 验证 GPU 是否工作中
```
nvidia-smi
```
你应该看到 Python 或者 uvicorn 进程正在使用 GPU。

# 二、java服务部署

## 🧩 1、启动 java服务工程
<img width="1384" alt="image" src="https://github.com/user-attachments/assets/de2f9da9-803a-4a79-822c-630803797b3a" />

## 🧩 2、使用 postman 调用 java 服务进行语音识别，以上传 1.mp3文件为例子 ，如下图：
![image](https://github.com/user-attachments/assets/d6e03a5f-f001-4c4d-b248-6136779cf176)

## 🧩 3、使用 postman 调用 java 服务进行语音识别任务查询，查看语音任务识别结果，如下图：
![image](https://github.com/user-attachments/assets/2313a902-1837-4847-937a-78b906669cfc)

# 三、流程架构图

## 🧩 1、流程图
用户语音上传/发起请求
        ↓
网关服务（Spring Cloud Gateway 资源感知 + 优化分配 或者 nginx 负载均衡转发）
        ↓
JAVA API 集群服务，任务中心（任务队列 + 元信息记录）
        ↓
Whisper服务 + LLM翻译服务
        ↓
翻译结果入库 & 客户回调通知

# 四、可靠性 & 可扩展性
## 🧩 1、考虑存在海量翻译任务的情况，设计出可以快速扩容的系统。
```
存在海量翻译任务的情况下，可以快速添加多个java api服务实例进行扩容，该服务可对接 spring cloud gateway、或者由 nginx做负载均衡，从而轻松实现扩容。
```
## 🧩 2、文件处理比较耗费内存资源，在进行任务分发时应该考虑不同节点内存的当前使用量：在充分使用内存资源的同时，避免出现 OutOfMemory 错误。
```
可以利用 java内置的OperatingSystemMXBean内存感知方法类，间隔 5 秒上报节点服务内存使用情况，再由节点中心根据内存使用高低排序优先分配任务节点。
```
## 🧩 3、支持故障转移与任务重做逻辑，保证不会有翻译任务被丢失。
```
故障转移（Failover）设计：定时心跳 + 掉线检测，每个 Worker 每 5s 上报一次心跳（worker_id, 时间戳），使用 Zookeeper Liveness-Probe 机制维护活跃节点，超过 30s 无心跳，则认为掉线，掉线的将不分配识别翻译任务
```
   
```
任务重做逻辑：用户提交记录先做持久化入库，标识记录状态，由异步 worket节点翻译成功后再标识成功状态，如果翻译失败将由各个 worker节点定时重新发起翻译处理
```

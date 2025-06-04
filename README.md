# 一、whisper 部署

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
  -F "file=@your_audio_file.mp3"
```
返回以下结果代表 whisper的环境成功
![image](https://github.com/user-attachments/assets/f3c926c1-0663-413d-ad1a-94917444a23f)

文档地址在 8000/docs，如下：
<img width="1470" alt="image" src="https://github.com/user-attachments/assets/d2e32245-84ea-4076-adb6-be11fdfab432" />


✅ 验证 GPU 是否工作中
```
nvidia-smi
```
你应该看到 Python 或者 uvicorn 进程正在使用 GPU。

# 二、java服务部署

<img width="1384" alt="image" src="https://github.com/user-attachments/assets/de2f9da9-803a-4a79-822c-630803797b3a" />

![image](https://github.com/user-attachments/assets/d6e03a5f-f001-4c4d-b248-6136779cf176)

![image](https://github.com/user-attachments/assets/2313a902-1837-4847-937a-78b906669cfc)



FROM ubuntu:22.04

# 安装基础依赖
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    wget \
    unzip \
    libxt6 \
    libxrender1 \
    libgl1-mesa-glx \
    libglib2.0-0 \
    ca-certificates \
    openjdk-11-jdk \
    && rm -rf /var/lib/apt/lists/*

# 复制 MATLAB Runtime
COPY MATLAB_Runtime_R2024a_Update_7_glnxa64.zip /tmp/

# 安装 MATLAB Runtime
RUN unzip /tmp/MATLAB_Runtime_R2024a_Update_7_glnxa64.zip -d /tmp/matlab && \
    cd /tmp/matlab && \
    chmod +x install && \
    ./install -mode silent -agreeToLicense yes -destinationFolder /usr/local/MATLAB/MATLAB_Runtime && \
    rm -rf /tmp/matlab /tmp/MATLAB_Runtime_R2024a_Update_7_glnxa64.zip

# 设置环境变量
ENV LD_LIBRARY_PATH=/usr/local/MATLAB/MATLAB_Runtime/R2024a/runtime/glnxa64:\
/usr/local/MATLAB/MATLAB_Runtime/R2024a/bin/glnxa64:\
/usr/local/MATLAB/MATLAB_Runtime/R2024a/sys/os/glnxa64:\
/usr/local/MATLAB/MATLAB_Runtime/R2024a/extern/bin/glnxa64

# 设置工作目录
WORKDIR /app

# 复制应用程序
COPY target/matpower-web-*.jar /app/app.jar
COPY lib/* /app/lib/

# 暴露端口
EXPOSE 8082

# 基于原始启动脚本的启动命令
CMD ["java", \
    "-Xmx1G", \
    "-Xms512M", \
    "-XX:+UseG1GC", \
    "-Dfile.encoding=UTF-8", \
    "-Dserver.port=8082", \
    "-Dspring.profiles.active=prod", \
    "-Djava.library.path=/usr/local/MATLAB/MATLAB_Runtime/R2024a/runtime/glnxa64:/usr/local/MATLAB/MATLAB_Runtime/R2024a/bin/glnxa64", \
    "-cp", "app.jar:lib/*:/usr/local/MATLAB/MATLAB_Runtime/R2024a/toolbox/javabuilder/jar/javabuilder.jar", \
    "org.springframework.boot.loader.JarLauncher"]

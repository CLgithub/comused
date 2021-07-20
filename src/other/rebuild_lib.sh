#!/bin/bash

projectName=$(basename "$PWD")  # 获取工程名称
newJarPath=./target/$projectName    # 定义新jar包路径

if [ -d $newJarPath ]; then
	rm -rf $newJarPath
fi

mkdir -p $newJarPath

jar -xf target/*.jar    # 解压源jar

mv *-INF $newJarPath/ 
mv org $newJarPath/ 

cp -r ./lib/*.jar $newJarPath/BOOT-INF/lib/ # 将lib下的jar都拷贝到需要打包的lib中

jars=$(ls lib)

for i in $jars; do
	echo - '"BOOT-INF/lib/'$i'"' >> $newJarPath/BOOT-INF/classpath.idx  # 将lib下的jar添加到类路径
done

cd $newJarPath
jar -cvfM0 $projectName.jar *   # 重新打包jar，c创建新档案，v显示详细，f指定档案文件名，M不创建条目的清单文件，0仅存储; 不使用任何 ZIP 压缩
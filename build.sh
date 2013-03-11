#!/bin/sh
if [ ./fxaa.vshader -nt ./fxaa.vshader.inl ]; then
	bin2c fxaa.vshader > fxaa.vshader.inl
fi
if [ ./fxaa.fshader -nt ./fxaa.fshader.inl ]; then
	bin2c fxaa.fshader > fxaa.fshader.inl
fi

android update project -p . -s

if [ "$1" = "nexus" ]; then
	export BUILD_MODULE=EMUya
	~/ndk/ndk-build V=1 NDK_DEBUG=0 NDK_OUT=obj-nexus
	if [ $? -eq 0 ]; then
		ant debug
		if [ $? -eq 0 ]; then
			mv bin/NativeActivity-debug.apk bin/NativeActivity-nexus.apk
			adb install -r bin/NativeActivity-nexus.apk
		fi
	fi
fi


if [ "$1" = "ouya" ]; then
	export BUILD_MODULE=EMUya
	~/ndk/ndk-build V=1 NDK_DEBUG=0 NDK_OUT=obj-ouya APP_CFLAGS=-DOUYA
	if [ $? -eq 0 ]; then
		ant debug
		if [ $? -eq 0 ]; then
			mv bin/NativeActivity-debug.apk bin/NativeActivity-ouya.apk
			adb install -r bin/NativeActivity-ouya.apk
		fi
	fi
fi


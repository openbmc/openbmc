require apt-native.inc

SRC_URI += "file://noconfigure.patch \
            file://no-curl.patch \
            file://gcc_4.x_apt-pkg-contrib-strutl.cc-Include-array-header.patch \
            file://gcc_4.x_Revert-avoid-changing-the-global-LC_TIME-for-Release.patch \
            file://gcc_4.x_Revert-use-de-localed-std-put_time-instead-rolling-o.patch"

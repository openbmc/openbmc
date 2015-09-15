MAJOR_VERSION = "2.26"
require util-linux.inc

# To support older hosts, we need to patch and/or revert
# some upstream changes.  Only do this for native packages.
OLDHOST = ""
OLDHOST_class-native = "file://util-linux-native.patch \
                        file://util-linux-native-qsort.patch \
			"

SRC_URI += "file://util-linux-ng-replace-siginterrupt.patch \
            file://util-linux-ng-2.16-mount_lock_path.patch \
            file://uclibc-__progname-conflict.patch \
            file://configure-sbindir.patch \
            file://fix-parallel-build.patch \
            ${OLDHOST} \
"
SRC_URI[md5sum] = "9bdf368c395f1b70325d0eb22c7f48fb"
SRC_URI[sha256sum] = "0e29bda142528a48a0a953c39ff63093651a4809042e1790fbd6aa8663fd9666"

CACHED_CONFIGUREVARS += "scanf_cv_alloc_modifier=ms"

EXTRA_OECONF_class-native = "${SHARED_EXTRA_OECONF} \
                             --disable-fallocate \
			     --disable-use-tty-group \
"
EXTRA_OECONF_class-nativesdk = "${SHARED_EXTRA_OECONF} \
                                --disable-fallocate \
				--disable-use-tty-group \
"

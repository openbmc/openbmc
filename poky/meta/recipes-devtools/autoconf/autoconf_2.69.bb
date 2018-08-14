require autoconf.inc

PR = "r11"

LICENSE = "GPLv2 & GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
		    file://COPYINGv3;md5=d32239bcb673463ab874e80d47fae504"
SRC_URI += "file://check-automake-cross-warning.patch \
	    file://autoreconf-exclude.patch \
	    file://autoreconf-gnuconfigize.patch \
            file://config_site.patch \
            file://remove-usr-local-lib-from-m4.patch \
            file://preferbash.patch \
            file://autotest-automake-result-format.patch \
            file://add_musl_config.patch \
            file://performance.patch \
            file://AC_HEADER_MAJOR-port-to-glibc-2.25.patch \
            file://autoconf-replace-w-option-in-shebangs-with-modern-use-warnings.patch \
           "

SRC_URI[md5sum] = "82d05e03b93e45f5a39b828dc9c6c29b"
SRC_URI[sha256sum] = "954bd69b391edc12d6a4a51a2dd1476543da5c6bbf05a95b59dc0dd6fd4c2969"

SRC_URI_append_class-native = " file://fix_path_xtra.patch"

EXTRA_OECONF += "ac_cv_path_M4=m4 ac_cv_prog_TEST_EMACS=no"

BBCLASSEXTEND = "native nativesdk"

require ${BPN}.inc

SRC_URI += "file://0001-Use-proc-self-exe-for-swig-swiglib-on-non-Win32-plat.patch \
            file://0001-configure-use-pkg-config-for-pcre-detection.patch \
            file://0001-Add-Node-7.x-aka-V8-5.2-support.patch \
            file://swig-3.0.12-Coverity-fix-issue-reported-for-SWIG_Python_FixMetho.patch \
            file://Python-Fix-new-GCC8-warnings-in-generated-code.patch \
            file://0001-Fix-generated-code-for-constant-expressions-containi.patch \
           "
SRC_URI[md5sum] = "82133dfa7bba75ff9ad98a7046be687c"
SRC_URI[sha256sum] = "7cf9f447ae7ed1c51722efc45e7f14418d15d7a1e143ac9f09a668999f4fc94d"


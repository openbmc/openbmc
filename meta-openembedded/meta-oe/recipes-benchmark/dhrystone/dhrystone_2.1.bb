SUMMARY = "Dhrystone CPU benchmark"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/PD;md5=b3597d12946881e13cb3b548d1173851"

SRC_URI = "http://www.netlib.org/benchmark/dhry-c;downloadfilename=dhry-c.shar \
           file://dhrystone.patch \
           file://dhrystone-c89.patch \
"
SRC_URI[sha256sum] = "038a7e9169787125c3451a6c941f3aca5db2d2f3863871afcdce154ef17f4e3e"

# Need to override Makefile variables
EXTRA_OEMAKE = "-e MAKEFLAGS="

do_unpack() {
    [ -d ${S} ] || mkdir -p ${S}
    cd ${S}
    sh ${DL_DIR}/dhry-c.shar
}
do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/dhry ${D}${bindir}
}

# Prevent procedure merging as required by dhrystone.c:
CFLAGS += "-fno-lto"
CFLAGS:append:toolchain-clang = " -Wno-error=implicit-function-declaration -Wno-error=deprecated-non-prototype -Wno-error=implicit-int"

LDFLAGS += "-fno-lto"

# http://errors.yoctoproject.org/Errors/Details/766887/
# dhry_1.c:101:3: error: implicit declaration of function 'strcpy' [-Wimplicit-function-declaration]
# dhry_1.c:149:5: error: implicit declaration of function 'Proc_5' [-Wimplicit-function-declaration]
# dhry_1.c:150:5: error: implicit declaration of function 'Proc_4' [-Wimplicit-function-declaration]
# dhry_1.c:156:19: error: implicit declaration of function 'Func_2'; did you mean 'Func_1'? [-Wimplicit-function-declaration]
# dhry_1.c:162:7: error: implicit declaration of function 'Proc_7' [-Wimplicit-function-declaration]
# dhry_1.c:167:5: error: implicit declaration of function 'Proc_8' [-Wimplicit-function-declaration]
# dhry_1.c:169:5: error: implicit declaration of function 'Proc_1' [-Wimplicit-function-declaration]
# dhry_1.c:176:9: error: implicit declaration of function 'Proc_6' [-Wimplicit-function-declaration]
# dhry_1.c:187:5: error: implicit declaration of function 'Proc_2' [-Wimplicit-function-declaration]
# dhry_1.c:287:1: error: return type defaults to 'int' [-Wimplicit-int]
# dhry_1.c:303:3: error: implicit declaration of function 'Proc_3'; did you mean 'Proc_1'? [-Wimplicit-function-declaration]
# dhry_1.c:321:1: error: return type defaults to 'int' [-Wimplicit-int]
# dhry_1.c:344:1: error: return type defaults to 'int' [-Wimplicit-int]
# dhry_1.c:359:1: error: return type defaults to 'int' [-Wimplicit-int]
# dhry_1.c:371:1: error: return type defaults to 'int' [-Wimplicit-int]
# dhry_1.c:73:1: error: return type defaults to 'int' [-Wimplicit-int]
# dhry_2.c:164:9: error: implicit declaration of function 'strcmp' [-Wimplicit-function-declaration]
# dhry_2.c:30:1: error: return type defaults to 'int' [-Wimplicit-int]
# dhry_2.c:39:9: error: implicit declaration of function 'Func_3' [-Wimplicit-function-declaration]
# dhry_2.c:64:1: error: return type defaults to 'int' [-Wimplicit-int]
# dhry_2.c:84:1: error: return type defaults to 'int' [-Wimplicit-int]
CFLAGS += "-Wno-error=implicit-int -Wno-error=implicit-function-declaration"

SUMMARY = "A fast usermode x86 and x86-64 emulator for Arm64 Linux"
HOMEPAGE = "https://github.com/FEX-Emu/FEX"
LICENSE = "MIT & BSL-1.0 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=70d20d502833c35d6d5a4f0ef5d9efcc"
LIC_FILES_CHKSUM += "file://External/range-v3/LICENSE.txt;md5=5dc23d5193abaedb6e42f05650004624"
LIC_FILES_CHKSUM += "file://External/xxhash/LICENSE;md5=13be6b481ff5616f77dda971191bb29b"

COMPATIBLE_HOST = "aarch64.*-linux"
COMPATIBLE_HOST:libc-musl = "null"

SRC_URI = " \
	git://github.com/FEX-Emu/FEX.git;name=fex;protocol=https;nobranch=1;tag=FEX-${PV} \
	git://github.com/Sonicadvance1/cpp-optparse.git;name=cpp-optparse;subdir=${S}/Source/Common/cpp-optparse;protocol=https;nobranch=1 \
	git://github.com/FEX-Emu/drm-headers.git;name=fex-drm;subdir=${S}/External/drm-headers;protocol=https;nobranch=1 \
	git://github.com/Cyan4973/xxHash.git;name=xxhash;subdir=${S}/External/xxhash;protocol=https;nobranch=1 \
	git://github.com/FEX-Emu/jemalloc.git;name=jemalloc;subdir=${S}/External/jemalloc;protocol=https;nobranch=1 \
	git://github.com/FEX-Emu/jemalloc.git;name=jemalloc-glibc;subdir=${S}/External/jemalloc_glibc;protocol=https;nobranch=1 \
	git://github.com/ericniebler/range-v3.git;name=range-v3;subdir=${S}/External/range-v3;protocol=https;nobranch=1 \
	git://github.com/FEX-Emu/robin-map.git;name=robin-map;subdir=${S}/External/robin-map;protocol=https;nobranch=1 \
"

SRCREV_FORMAT = "fex"
SRCREV_fex = "1188c90c10569ca800d7a99c11e59cfeab5e2cc9"
SRCREV_cpp-optparse = "9f94388a339fcbb0bc95c17768eb786c85988f6e"
SRCREV_fex-drm = "3e49836995c1dcb3df709440ad2f270b569c6a5f"
SRCREV_xxhash = "e626a72bc2321cd320e953a0ccf1584cad60f363"
SRCREV_jemalloc = "97d986993dc735a2022856e7e9fdfa1180e8527a"
SRCREV_jemalloc-glibc = "8436195ad5e1bc347d9b39743af3d29abee59f06"
SRCREV_robin-map = "d5683d9f1891e5b04e3e3b2192b5349dc8d814ea"
SRCREV_range-v3 = "ca1388fb9da8e69314dda222dc7b139ca84e092f"

DEPENDS = " \
    catch2 \
    fmt \
    libdrm  \
    nasm-native \
    vulkan-headers \
"

PACKAGECONFIG = ""
PACKAGECONFIG[qt] = "-DBUILD_FEXCONFIG=ON,-DBUILD_FEXCONFIG=OFF,qtbase qttools-native qtquick3d"

EXTRA_OECMAKE += " \
	-DBUILD_TESTING=OFF \
	-DENABLE_VIXL_DISASSEMBLER=OFF \
	-DENABLE_VIXL_SIMULATOR=OFF \
	-DDATA_DIRECTORY=${datadir} \
	-DQT_HOST_PATH:PATH=${RECIPE_SYSROOT_NATIVE}${prefix_native} \
"

inherit cmake ccache pkgconfig

LDFLAGS += "-fuse-ld=lld"

FILES:${PN} += "${datadir} ${libdir}/binfmt.d ${libdir}/libFEXCore.so"
FILES:${PN}-dev = "${includedir}"

# At the time of writing this, this recipe has no historical CVEs associated, and couldn't
# find a CPE to set. So ignore these unrelated vulnerabilities.
CVE_STATUS_GROUPS = "CVE_STATUS_WRONG_CPE"
CVE_STATUS_WRONG_CPE[status] = "cpe-incorrect: These vulnerabilities are for unrelated Fram's Fast File-EXchange application"
CVE_STATUS_WRONG_CPE = "CVE-2012-0869 CVE-2012-1293 CVE-2014-3875 CVE-2014-3876 CVE-2014-3877"

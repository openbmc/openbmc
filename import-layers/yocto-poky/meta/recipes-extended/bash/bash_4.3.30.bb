require bash.inc

# GPLv2+ (< 4.0), GPLv3+ (>= 4.0)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/bash/${BP}.tar.gz;name=tarball \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-031;apply=yes;striplevel=0;name=patch031 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-032;apply=yes;striplevel=0;name=patch032 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-033;apply=yes;striplevel=0;name=patch033 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-034;apply=yes;striplevel=0;name=patch034 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-035;apply=yes;striplevel=0;name=patch035 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-036;apply=yes;striplevel=0;name=patch036 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-037;apply=yes;striplevel=0;name=patch037 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-038;apply=yes;striplevel=0;name=patch038 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-039;apply=yes;striplevel=0;name=patch039 \
           file://execute_cmd.patch;striplevel=0 \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://fix-run-coproc-run-heredoc-run-execscript-run-test-f.patch \
           file://run-ptest \
           "

SRC_URI[tarball.md5sum] = "a27b3ee9be83bd3ba448c0ff52b28447"
SRC_URI[tarball.sha256sum] = "317881019bbf2262fb814b7dd8e40632d13c3608d2f237800a8828fbb8a640dd"

SRC_URI[patch031.md5sum] = "236df1ac1130a033ed0dbe2d2115f28f"
SRC_URI[patch031.sha256sum] = "cd529f59dd0f2fdd49d619fe34691da6f0affedf87cc37cd460a9f3fe812a61d"
SRC_URI[patch032.md5sum] = "2360f7e79cfb28526f80021025ea5909"
SRC_URI[patch032.sha256sum] = "889357d29a6005b2c3308ca5b6286cb223b5e9c083219e5db3156282dd554f4a"
SRC_URI[patch033.md5sum] = "b551c4ee7b8713759e4143499d0bbd48"
SRC_URI[patch033.sha256sum] = "fb2a7787a13fbe027a7335aca6eb3c21cdbd813e9edc221274b6a9d8692eaa16"
SRC_URI[patch034.md5sum] = "c9a56fbe0348e05a886dff97f2872b74"
SRC_URI[patch034.sha256sum] = "f1694f04f110defe1330a851cc2768e7e57ddd2dfdb0e3e350ca0e3c214ff889"
SRC_URI[patch035.md5sum] = "e564e8ab44ed1ca3a4e315a9f6cabdc9"
SRC_URI[patch035.sha256sum] = "370d85e51780036f2386dc18c5efe996eba8e652fc1973f0f4f2ab55a993c1e3"
SRC_URI[patch036.md5sum] = "b00ff66c41a7c0f06e191200981980b0"
SRC_URI[patch036.sha256sum] = "ac5f82445b36efdb543dbfae64afed63f586d7574b833e9aa9cd5170bc5fd27c"
SRC_URI[patch037.md5sum] = "be2a7b05f6ae560313f3c9d5f7127bda"
SRC_URI[patch037.sha256sum] = "33f170dd7400ab3418d749c55c6391b1d161ef2de7aced1873451b3a3fca5813"
SRC_URI[patch038.md5sum] = "61e0522830b24fbe8c0d1b010f132470"
SRC_URI[patch038.sha256sum] = "adbeaa500ca7a82535f0e88d673661963f8a5fcdc7ad63445e68bf5b49786367"
SRC_URI[patch039.md5sum] = "a4775487abe958536751c8ce53cdf6f9"
SRC_URI[patch039.sha256sum] = "ab94dced2215541097691f60c3eb323cc28ef2549463e6a5334bbcc1e61e74ec"

BBCLASSEXTEND = "nativesdk"

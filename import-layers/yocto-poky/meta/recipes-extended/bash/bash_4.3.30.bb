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
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-040;apply=yes;striplevel=0;name=patch040 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-041;apply=yes;striplevel=0;name=patch041 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-042;apply=yes;striplevel=0;name=patch042 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-043;apply=yes;striplevel=0;name=patch043 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-044;apply=yes;striplevel=0;name=patch044 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-045;apply=yes;striplevel=0;name=patch045 \
           ${GNU_MIRROR}/bash/bash-4.3-patches/bash43-046;apply=yes;striplevel=0;name=patch046 \
           file://execute_cmd.patch;striplevel=0 \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://fix-run-coproc-run-heredoc-run-execscript-run-test-f.patch \
           file://run-ptest \
           file://fix-run-builtins.patch \
           file://0001-help-fix-printf-format-security-warning.patch \
           file://fix-run-intl.patch \
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
SRC_URI[patch040.md5sum] = "80d3587c58854e226055ef099ffeb535"
SRC_URI[patch040.sha256sum] = "84bb396b9262992ca5424feab6ed3ec39f193ef5c76dfe4a62b551bd8dd9d76b"
SRC_URI[patch041.md5sum] = "20bf63eef7cb441c0b1cc49ef3191d03"
SRC_URI[patch041.sha256sum] = "4ec432966e4198524a7e0cd685fe222e96043769c9613e66742ac475db132c1a"
SRC_URI[patch042.md5sum] = "70790646ae61e207c995e44931390e50"
SRC_URI[patch042.sha256sum] = "ac219322db2791da87a496ee6e8e5544846494bdaaea2626270c2f73c1044919"
SRC_URI[patch043.md5sum] = "855a46955cb251534e80b4732b748e37"
SRC_URI[patch043.sha256sum] = "47a8a3c005b46e25821f4d8f5ccb04c1d653b1c829cb40568d553dc44f7a6180"
SRC_URI[patch044.md5sum] = "29623d3282fcbb37e1158136509b5bb8"
SRC_URI[patch044.sha256sum] = "9338820630bf67373b44d8ea68409f65162ea7a47b9b29ace06a0aed12567f99"
SRC_URI[patch045.md5sum] = "4473244ca5abfd4b018ea26dc73e7412"
SRC_URI[patch045.sha256sum] = "ba6ec3978e9eaa1eb3fabdaf3cc6fdf8c4606ac1c599faaeb4e2d69864150023"
SRC_URI[patch046.md5sum] = "7e5fb09991c077076b86e0e057798913"
SRC_URI[patch046.sha256sum] = "b3b456a6b690cd293353f17e22d92a202b3c8bce587ae5f2667c20c9ab6f688f"

BBCLASSEXTEND = "nativesdk"

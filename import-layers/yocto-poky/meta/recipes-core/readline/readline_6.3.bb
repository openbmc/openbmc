require readline.inc

SRC_URI += "${GNU_MIRROR}/readline/readline-6.3-patches/readline63-001;name=patch1;apply=yes;striplevel=0 \
            ${GNU_MIRROR}/readline/readline-6.3-patches/readline63-002;name=patch2;apply=yes;striplevel=0 \
            ${GNU_MIRROR}/readline/readline-6.3-patches/readline63-003;name=patch3;apply=yes;striplevel=0 \
            ${GNU_MIRROR}/readline/readline-6.3-patches/readline63-004;name=patch4;apply=yes;striplevel=0 \
            ${GNU_MIRROR}/readline/readline-6.3-patches/readline63-005;name=patch5;apply=yes;striplevel=0 \
            ${GNU_MIRROR}/readline/readline-6.3-patches/readline63-006;name=patch6;apply=yes;striplevel=0 \
            ${GNU_MIRROR}/readline/readline-6.3-patches/readline63-007;name=patch7;apply=yes;striplevel=0 \
            ${GNU_MIRROR}/readline/readline-6.3-patches/readline63-008;name=patch8;apply=yes;striplevel=0 \
            file://configure-fix.patch \
            file://config-dirent-symbols.patch \
            file://norpath.patch"

SRC_URI[archive.md5sum] = "33c8fb279e981274f485fd91da77e94a"
SRC_URI[archive.sha256sum] = "56ba6071b9462f980c5a72ab0023893b65ba6debb4eeb475d7a563dc65cafd43"

SRC_URI[patch1.md5sum]    = "4343f5ea9b0f42447f102fb61576b398"
SRC_URI[patch1.sha256sum] = "1a79bbb6eaee750e0d6f7f3d059b30a45fc54e8e388a8e05e9c3ae598590146f"
SRC_URI[patch2.md5sum]    = "700295212f7e2978577feaee584afddb"
SRC_URI[patch2.sha256sum] = "39e304c7a526888f9e112e733848215736fb7b9d540729b9e31f3347b7a1e0a5"
SRC_URI[patch3.md5sum]    = "af4963862f5156fbf9111c2c6fa86ed7"
SRC_URI[patch3.sha256sum] = "ec41bdd8b00fd884e847708513df41d51b1243cecb680189e31b7173d01ca52f"
SRC_URI[patch4.md5sum]    = "11f9def89803a5052db3ba72394ce14f"
SRC_URI[patch4.sha256sum] = "4547b906fb2570866c21887807de5dee19838a60a1afb66385b272155e4355cc"
SRC_URI[patch5.md5sum]    = "93721c31cd225393f80cb3aadb165544"
SRC_URI[patch5.sha256sum] = "877788f9228d1a9907a4bcfe3d6dd0439c08d728949458b41208d9bf9060274b"
SRC_URI[patch6.md5sum]    = "71dc6ecce66d1489b96595f55d142a52"
SRC_URI[patch6.sha256sum] = "5c237ab3c6c97c23cf52b2a118adc265b7fb411b57c93a5f7c221d50fafbe556"
SRC_URI[patch7.md5sum]    = "062a08ed60679d3c4878710b3d595b65"
SRC_URI[patch7.sha256sum] = "4d79b5a2adec3c2e8114cbd3d63c1771f7c6cf64035368624903d257014f5bea"
SRC_URI[patch8.md5sum]    = "ee1c04072154826870848d8b218d7b04"
SRC_URI[patch8.sha256sum] = "3bc093cf526ceac23eb80256b0ec87fa1735540d659742107b6284d635c43787"


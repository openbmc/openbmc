SUMMARY = "Vendor Package Management for Golang"
HOMEPAGE = "https://github.com/Masterminds/glide"
DESCRIPTION = "Glide is a Vendor Package Management for Golang"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=54905cf894f8cc416a92f4fc350c35b2"

GO_IMPORT = "github.com/Masterminds/glide"
SRC_URI = "git://${GO_IMPORT};branch=master;protocol=https"
SRCREV = "8ed5b9292379d86c39592a7e6a58eb9c903877cf"

inherit go

# New Go versions has Go modules support enabled by default and cause the Glide
# tool build to fail.
export GO111MODULE = "off"

RDEPENDS:${PN}-dev += "bash"
RDEPENDS:${PN}-ptest += "bash"

BBCLASSEXTEND = "native nativesdk"

# for x86 ends with textrel in ${PN}
# http://errors.yoctoproject.org/Errors/Details/185631/
# ERROR: QA Issue: ELF binary '/work/i586-oe-linux/glide/0.13.1-r0/packages-split/glide/usr/bin/glide' has relocations in .text [textrel]
INSANE_SKIP:${PN} += "textrel"

# for aarch64 ends with textrel in ${PN}-ptest
# http://errors.yoctoproject.org/Errors/Details/185633/
# ERROR: QA Issue: ELF binary '/work/aarch64-oe-linux/glide/0.13.1-r0/packages-split/glide-ptest/usr/lib/glide/ptest/github.com/Masterminds/glide/glide.test' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/glide/0.13.1-r0/packages-split/glide-ptest/usr/lib/glide/ptest/github.com/Masterminds/glide/dependency/dependency.test' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/glide/0.13.1-r0/packages-split/glide-ptest/usr/lib/glide/ptest/github.com/Masterminds/glide/repo/repo.test' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/glide/0.13.1-r0/packages-split/glide-ptest/usr/lib/glide/ptest/github.com/Masterminds/glide/mirrors/mirrors.test' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/glide/0.13.1-r0/packages-split/glide-ptest/usr/lib/glide/ptest/github.com/Masterminds/glide/cfg/cfg.test' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/glide/0.13.1-r0/packages-split/glide-ptest/usr/lib/glide/ptest/github.com/Masterminds/glide/godep/strip/strip.test' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/glide/0.13.1-r0/packages-split/glide-ptest/usr/lib/glide/ptest/github.com/Masterminds/glide/path/path.test' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/glide/0.13.1-r0/packages-split/glide-ptest/usr/lib/glide/ptest/github.com/Masterminds/glide/tree/tree.test' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/glide/0.13.1-r0/packages-split/glide-ptest/usr/lib/glide/ptest/github.com/Masterminds/glide/util/util.test' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/glide/0.13.1-r0/packages-split/glide-ptest/usr/lib/glide/ptest/github.com/Masterminds/glide/action/action.test' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/glide/0.13.1-r0/packages-split/glide-ptest/usr/lib/glide/ptest/github.com/Masterminds/glide/cache/cache.test' has relocations in .text [textrel]
INSANE_SKIP:${PN}-ptest += "textrel"

# fails to run task compile_ptest_base on mips
PTEST_ENABLED:mipsarch = "0"

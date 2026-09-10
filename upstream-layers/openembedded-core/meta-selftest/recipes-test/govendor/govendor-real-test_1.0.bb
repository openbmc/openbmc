SUMMARY = "go-vendor integration test: recipetool-go-test"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=4e3933dd47afbf115e484d11385fb3bd"

# Only meant to be built directly by GoVendorTaskTests (bitbake('%s -c go_vendor')),
# not as part of a general world build.
EXCLUDE_FROM_WORLD = "1"

# Native variant lets GoVendorTaskTests.test_do_compile_succeeds exercise the
# real go toolchain without requiring a full target Go cross-toolchain.
BBCLASSEXTEND = "native"

inherit go-vendor

GO_IMPORT = "git.yoctoproject.org/recipetool-go-test"

# modules.txt for recipetool-go-test at SRCREV:
#   - github.com/godbus/dbus/v5 v5.1.0  (external, fetched via go_src_uri)
#   - github.com/matryer/is v1.4.1       (local replacement => ./is)
# Use go mod vendor to generate the modules.txt
SRC_URI = "\
    git://${GO_IMPORT};name=recipetool-go-test;branch=main;protocol=https;nobranch=1;destsuffix=${GO_SRCURI_DESTSUFFIX} \
    ${@go_src_uri('github.com/godbus/dbus', 'v5.1.0', path='github.com/godbus/dbus/v5', pathmajor='/v5')} \
    file://modules.txt \
    "
SRCREV_FORMAT = "recipetool-go-test"
# git.yoctoproject.org/recipetool-go-test main branch HEAD
SRCREV_recipetool-go-test = "c3e213c01b6c1406b430df03ef0d1ae77de5d2f7"
SRCREV_github.com.godbus.dbus.v5 = "e523abc905595cf17fb0001a7d77eaaddfaa216d"

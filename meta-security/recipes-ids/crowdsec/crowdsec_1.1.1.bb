SUMMARY = "CrowdSec is a free, modern & collaborative behavior detection engine, coupled with a global IP reputation network."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=105e75b680b2ab82fa5718661b41f3bf"

SRC_URI = "git://github.com/crowdsecurity/crowdsec.git;branch=master;protocol=https"
SRCREV = "73e0bbaf93070f4a640eb5a22212b5dcf26699de"

DEPENDS = "jq-native"

GO_IMPORT = "import"

inherit go

S = "${UNPACKDIR}/git"

do_compile() {
    export GOARCH="${TARGET_GOARCH}"
    export GOROOT="${STAGING_LIBDIR_NATIVE}/${TARGET_SYS}/go"

    # Pass the needed cflags/ldflags so that cgo
    # can find the needed headers files and libraries
    export CGO_ENABLED="1"
    export CFLAGS=""
    export LDFLAGS=""
    export CGO_CFLAGS="${BUILDSDK_CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
    export CGO_LDFLAGS="${BUILDSDK_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

    cd ${S}/src/import
    oe_runmake release
}

do_install_ () {
   chmod +x -R --silent ${B}/pkg
}


INSANE_SKIP:${PN} = "already-stripped"
INSANE_SKIP:${PN}-dev = "ldflags"

RDEPENDS:${PN} = "go"
RDEPENDS:${PN}-dev = "bash"

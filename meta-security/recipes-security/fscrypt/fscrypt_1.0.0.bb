SUMMARY = "fscrypt is a high-level tool for the management of Linux filesystem encryption"
DESCIPTION = "fscrypt manages metadata, key generation, key wrapping, PAM integration, \
and provides a uniform interface for creating and modifying encrypted directories. For \
a small, low-level tool that directly sets policies, see fscryptctl \
(https://github.com/google/fscryptcl)."
HOMEPAGE = "https://github.com/google/fscrypt"
SECTION = "base"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BBCLASSEXTEND = "native nativesdk"

# fscrypt depends on go and libpam
DEPENDS += "go-dep-native libpam"

SRCREV = "92b1e9a8670ccd3916a7d24a06cab1e4c9815bc4"
SRC_URI = "git://github.com/google/fscrypt.git"
GO_IMPORT = "import"

S = "${WORKDIR}/git"

inherit go
inherit goarch

do_compile() {
	export GOARCH=${TARGET_GOARCH}
	export GOROOT="${STAGING_LIBDIR_NATIVE}/${TARGET_SYS}/go"
	export GOPATH="${WORKDIR}/git"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

	cd ${S}/src/${GO_IMPORT}
	oe_runmake

	# Golang forces permissions to 0500 on directories and 0400 on files in
	# the module cache which prevents us from easily cleaning up the build
	# directory. Let's just fix the permissions here so we don't have to
	# hack the clean tasks.
	chmod -R u+w ${S}/pkg/mod
}

do_install() {
	install -d ${D}/${bindir}
	install ${S}/src/${GO_IMPORT}/bin/fscrypt ${D}/${bindir}/fscrypt
}

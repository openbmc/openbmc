HOMEPAGE = "https://github.com/opencontainers/image-tools"
SUMMARY = "A collection of tools for working with the OCI image format specification"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS = "\
           oci-image-spec \
           oci-runtime-spec \
           go-digest \
           go-errors \
           spf13-cobra \
           spf13-pflag \
          "

SRC_URI = "git://github.com/opencontainers/image-tools.git \
           file://0001-image-manifest-Recursively-remove-pre-existing-entri.patch \
           file://0002-image-manifest-Split-unpackLayerEntry-into-its-own-f.patch"

SRCREV = "4abe1a166f9be97e8e71b1bb4d7599cc29323011"
PV = "0.2.0-dev+git${SRCPV}"
GO_IMPORT = "import"

inherit goarch
inherit go

# This disables seccomp and apparmor, which are on by default in the
# go package. 
EXTRA_OEMAKE="BUILDTAGS=''"

do_compile() {
	export GOARCH="${TARGET_GOARCH}"
	export GOROOT="${STAGING_LIBDIR_NATIVE}/${TARGET_SYS}/go"
	# Setup vendor directory so that it can be used in GOPATH.
	#
	# Go looks in a src directory under any directory in GOPATH but riddler
	# uses 'vendor' instead of 'vendor/src'. We can fix this with a symlink.
	#
	# We also need to link in the ipallocator directory as that is not under
	# a src directory.
	ln -sfn . "${S}/src/import/vendor/src"
	mkdir -p "${S}/src/import/vendor/src/github.com/opencontainers/image-tools/"
	ln -sfn "${S}/src/import/image" "${S}/src/import/vendor/src/github.com/opencontainers/image-tools/image"
	ln -sfn "${S}/src/import/version" "${S}/src/import/vendor/src/github.com/opencontainers/image-tools/version"
	export GOPATH="${S}/src/import/vendor"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${BUILDSDK_CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${BUILDSDK_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	cd ${S}/src/import

	oe_runmake tool
}

do_install() {
	install -d ${D}/${sbindir}
	install ${S}/src/import/oci-image-tool ${D}/${sbindir}/
}

INSANE_SKIP_${PN} += "ldflags"

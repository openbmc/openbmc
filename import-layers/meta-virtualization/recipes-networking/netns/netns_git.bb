HOMEPAGE = "https://github.com/jfrazelle/netns"
SUMMARY = "Runc hook for setting up default bridge networking."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=20ce4c6a4f32d6ee4a68e3a7506db3f1"

SRC_URI = "git://github.com/jessfraz/netns;branch=master \
           file://0001-Use-correct-go-cross-compiler.patch \
          "
SRCREV = "74e23a0e5c4e7ac011aafcc4623586c196f1b3ef"
PV = "0.2.1"
GO_IMPORT = "import"

S = "${WORKDIR}/git"

inherit goarch
inherit go

do_compile() {
	export GOARCH="${TARGET_GOARCH}"
	export GOROOT="${STAGING_LIBDIR_NATIVE}/${TARGET_SYS}/go"
	# Setup vendor directory so that it can be used in GOPATH.
	#
	# Go looks in a src directory under any directory in GOPATH but netns
	# uses 'vendor' instead of 'vendor/src'. We can fix this with a symlink.
	#
	# We also need to link in the ipallocator and version directories as
	# they are not under the src directory.
	ln -sfn . "${S}/src/import/vendor/src"
	mkdir -p "${S}/src/import/vendor/src/github.com/jessfraz/netns"
	ln -sfn "${S}/src/import/ipallocator" "${S}/src/import/vendor/src/github.com/jessfraz/netns/ipallocator"
	ln -sfn "${S}/src/import/version" "${S}/src/import/vendor/src/github.com/jessfraz/netns/version"
	export GOPATH="${S}/src/import/vendor"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${BUILDSDK_CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${BUILDSDK_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

	cd ${S}/src/import
	oe_runmake static
}

do_install() {
	install -d ${D}/${sbindir}
	install ${S}/src/import/netns ${D}/${sbindir}/netns
}

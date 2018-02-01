HOMEPAGE = "https://github.com/jfrazelle/netns"
SUMMARY = "Runc hook for setting up default bridge networking."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20ce4c6a4f32d6ee4a68e3a7506db3f1"

SRC_URI = "git://github.com/jessfraz/netns;branch=master"
SRCREV = "85b1ab9fcccbaa404a2636b52a48bbde02437cf7"
PV = "0.1.0+git${SRCPV}"

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
	# We also need to link in the ipallocator directory as that is not under
	# a src directory.
	ln -sfn . "${S}/vendor/src"
	mkdir -p "${S}/vendor/src/github.com/jessfraz/netns"
	ln -sfn "${S}/ipallocator" "${S}/vendor/src/github.com/jessfraz/netns/ipallocator"
	export GOPATH="${S}/vendor"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${BUILDSDK_CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${BUILDSDK_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

	oe_runmake static
}

do_install() {
	install -d ${D}/${sbindir}
	install ${S}/netns ${D}/${sbindir}/netns
}

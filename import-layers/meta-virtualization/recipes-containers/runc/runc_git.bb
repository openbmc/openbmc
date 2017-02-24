HOMEPAGE = "https://github.com/opencontainers/runc"
SUMMARY = "runc container cli tools"
DESCRIPTION = "runc is a CLI tool for spawning and running containers according to the OCI specification."

# Note: this rev is before the required protocol field, update when all components
#       have been updated to match.
SRCREV = "1cdaa709f151b61cee2bdaa09d8e5d2b58a8ba72"
SRC_URI = "\
	git://github.com/opencontainers/runc;branch=master \
	"

# Apache-2.0 for containerd
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=435b266b3899aa8a959f17d41c56def8"

S = "${WORKDIR}/git"

RUNC_VERSION = "1.0.0-rc1"
PV = "${RUNC_VERSION}+git${SRCREV}"

DEPENDS = "go-cross \
    "
RRECOMMENDS_${PN} = "lxc docker"

LIBCONTAINER_PACKAGE="github.com/opencontainers/runc/libcontainer"

do_configure[noexec] = "1"
EXTRA_OEMAKE="BUILDTAGS=''"

inherit go-osarchmap

do_compile() {
	export GOARCH="${TARGET_GOARCH}"

	# Set GOPATH. See 'PACKAGERS.md'. Don't rely on
	# docker to download its dependencies but rather
	# use dependencies packaged independently.
	cd ${S}
	rm -rf .gopath
	dname=`dirname "${LIBCONTAINER_PACKAGE}"`
	bname=`basename "${LIBCONTAINER_PACKAGE}"`
	mkdir -p .gopath/src/${dname}

	(cd .gopath/src/${dname}; ln -sf ../../../../../${bname} ${bname})
	export GOPATH="${S}/.gopath:${S}/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go"
	cd -

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CFLAGS=""
	export LDFLAGS=""

        oe_runmake static
}

do_install() {
	mkdir -p ${D}/${bindir}

	cp ${S}/runc ${D}/${bindir}/runc
	ln -sf runc ${D}/${bindir}/docker-runc
}

INHIBIT_PACKAGE_STRIP = "1"

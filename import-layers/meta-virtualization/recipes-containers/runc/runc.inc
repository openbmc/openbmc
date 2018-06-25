HOMEPAGE = "https://github.com/opencontainers/runc"
SUMMARY = "runc container cli tools"
DESCRIPTION = "runc is a CLI tool for spawning and running containers according to the OCI specification."

# Apache-2.0 for containerd
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=435b266b3899aa8a959f17d41c56def8"

S = "${WORKDIR}/git"

PV = "${RUNC_VERSION}+git${SRCPV}"

inherit go
inherit goarch

RRECOMMENDS_${PN} = "lxc docker"
PROVIDES += "virtual/runc"
RPROVIDES_${PN} = "virtual/runc"

GO_IMPORT = "import"

LIBCONTAINER_PACKAGE="github.com/opencontainers/runc/libcontainer"

do_configure[noexec] = "1"
EXTRA_OEMAKE="BUILDTAGS='' GO=${GO}"

do_compile() {
	# Set GOPATH. See 'PACKAGERS.md'. Don't rely on
	# docker to download its dependencies but rather
	# use dependencies packaged independently.
	cd ${S}/src/import
	rm -rf .gopath
	dname=`dirname "${LIBCONTAINER_PACKAGE}"`
	bname=`basename "${LIBCONTAINER_PACKAGE}"`
	mkdir -p .gopath/src/${dname}

	(cd .gopath/src/${dname}; ln -sf ../../../../../${bname} ${bname})
	export GOPATH="${S}/src/import/.gopath:${S}/src/import/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go"

	# Fix up symlink for go-cross compiler
	rm -f ${S}/src/import/vendor/src
	ln -sf ./ ${S}/src/import/vendor/src

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export GO=${GO}

	export CFLAGS=""
	export LDFLAGS=""

	oe_runmake static
}

do_install() {
	mkdir -p ${D}/${bindir}

	cp ${S}/src/import/runc ${D}/${bindir}/runc
	ln -sf runc ${D}/${bindir}/docker-runc
}

INHIBIT_PACKAGE_STRIP = "1"

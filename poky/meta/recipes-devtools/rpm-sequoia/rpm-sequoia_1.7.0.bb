SUMMARY = "An OpenPGP backend for rpm using Sequoia PGP"
HOMEPAGE = "https://sequoia-pgp.org/"

# The license line is taken verbatim from Fedora's specfile
# with formatting differences for Yocto. (AND -> &, OR -> |)
# https://src.fedoraproject.org/rpms/rust-rpm-sequoia/blob/rawhide/f/rust-rpm-sequoia.spec
LICENSE = "LGPL-2.0-or-later & Apache-2.0 & BSL-1.0 & MIT & Unicode-DFS-2016 & (Apache-2.0 | MIT) & (MIT | Apache-2.0 | Zlib) & (Unlicense | MIT)"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f0ff5ff7747cf7d394079c6ae87f5f0c"

DEPENDS = "openssl"

inherit pkgconfig rust cargo cargo-update-recipe-crates

SRC_URI = "git://github.com/rpm-software-management/rpm-sequoia.git;protocol=https;branch=main"

SRCREV = "0667e04ae7fb8cf0490919978d69883d16400e41"

S = "${UNPACKDIR}/git"

require ${BPN}-crates.inc

CARGO_BUILD_FLAGS += "--no-default-features --features crypto-openssl"
CARGO_INSTALL_LIBRARIES = "1"

do_compile:prepend () {
	# rpm-sequoia.pc is generated in the source directory
	# but the target directory does not exist there.
	mkdir -p ${S}/target/release

	# From rpm-sequoia's README.md:
	#
	# We also set two environment variables when calling `cargo build`:
	# * `PREFIX` is the prefix that will be used in the generated
	#   `rpm-sequoia.pc` file. It defaults to `/usr/local`.
	# * `LIBDIR` is the installed library path listed in the generated
	#   metadata. It can be an absolute path or one based on `${prefix}`,
	#   and defaults to `${prefix}/lib`.

	export PREFIX="${prefix}"
	export LIBDIR="${libdir}"
}

do_install:append () {
	# Move the library to the correct location expected by rpm-sequoia.pc
	mkdir -p ${D}${libdir}
	mv ${D}${rustlibdir}/librpm_sequoia.so ${D}${libdir}/librpm_sequoia.so.1
	ln -s librpm_sequoia.so.1 ${D}${libdir}/librpm_sequoia.so

	rmdir -p --ignore-fail-on-non-empty ${D}${rustlibdir}

	# rpm-sequoia does not install its pkgconfig file. Do it manually.
	mkdir -p ${D}${libdir}/pkgconfig
	install -m644 ${S}/target/release/rpm-sequoia.pc ${D}${libdir}/pkgconfig
}

RDEPENDS:${PN} = "rpm-sequoia-crypto-policy"
PACKAGE_WRITE_DEPS += "rpm-sequoia-crypto-policy-native"

BBCLASSEXTEND = "native"

SUMMARY = "Crypto policy for rpm-sequoia"
HOMEPAGE = "https://gitlab.com/redhat-crypto/fedora-crypto-policies/"

LICENSE = "LGPL-2.1-or-later"

LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=a6f89e2100d9b6cdffcea4f398e37343"

# Python 3.11+ is needed to build fedora-crypto-policies
inherit allarch python3native

SRC_URI = "git://gitlab.com/redhat-crypto/fedora-crypto-policies.git;protocol=https;branch=master"

SRCREV = "032b418a6db842f0eab330eb5909e4604e888728"
UPSTREAM_CHECK_COMMITS = "1"

S = "${UNPACKDIR}/git"

do_compile () {
	# Remove most policy variants, leave DEFAULT.pol
	# It speeds up the build and we only need DEFAULT/rpm-sequoia.
	rm -f $(ls -1 policies/*.pol | grep -v DEFAULT.pol) || echo nothing to delete

	# Don't validate openssh and gnutls policy variants.
	# Validation may fail and these variants are not needed.
	export OLD_OPENSSH=1
	export OLD_GNUTLS=1

	make ASCIIDOC=echo XSLTPROC=echo
}

do_install () {
	install -d -m755 ${D}${datadir}/crypto-policies/back-ends
	install -m644 ${S}/output/DEFAULT/rpm-sequoia.txt ${D}${datadir}/crypto-policies/back-ends/rpm-sequoia.config
}

FILES:${PN} = "${datadir}/crypto-policies/back-ends/*"

BBCLASSEXTEND = "native"

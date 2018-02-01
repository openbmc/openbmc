inherit distutils
require python-pycrypto.inc

SRC_URI += "file://cross-compiling.patch \
            file://CVE-2013-7459.patch \
           "

# We explicitly call distutils_do_install, since we want it to run, but
# *don't* want the autotools install to run, since this package doesn't
# provide a "make install" target.
do_install() {
       distutils_do_install
}

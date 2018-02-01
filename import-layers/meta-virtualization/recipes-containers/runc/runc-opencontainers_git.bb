include runc.inc

SRCREV = "75f8da7c889acc4509a0cf6f0d3a8f9584778375"
SRC_URI = "git://github.com/opencontainers/runc;branch=master \
          "
RUNC_VERSION = "1.0.0-rc3"
PROVIDES += "virtual/runc"
RPROVIDES_${PN} = "virtual/runc"

do_compile_prepend() {
	# Go looks in a src directory under any directory in GOPATH but
	# runc-opencontainers uses 'vendor' instead of 'vendor/src'. We can fix
	# this with a symlink.
	ln -sfn . "${S}/vendor/src"
}

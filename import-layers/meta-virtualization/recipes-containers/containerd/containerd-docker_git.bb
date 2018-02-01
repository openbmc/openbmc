include containerd.inc

SRCREV = "03e5862ec0d8d3b3f750e19fca3ee367e13c090e"
SRC_URI = "\
	git://github.com/docker/containerd.git;branch=docker-1.13.x \
	"
CONTAINERD_VERSION = "0.2.3"

PROVIDES += "virtual/containerd"
RPROVIDES_${PN} = "virtual/containerd"

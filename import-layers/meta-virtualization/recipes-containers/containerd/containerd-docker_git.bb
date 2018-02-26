SRCREV = "3addd840653146c90a254301d6c3a663c7fd6429"
SRC_URI = "\
	git://github.com/docker/containerd.git;branch=v0.2.x;destsuffix=git/src/github.com/containerd/containerd \
	"

include containerd.inc

CONTAINERD_VERSION = "v0.2.x"
S = "${WORKDIR}/git/src/github.com/containerd/containerd"

PROVIDES += "virtual/containerd"
RPROVIDES_${PN} = "virtual/containerd"

DEPENDS += "btrfs-tools"

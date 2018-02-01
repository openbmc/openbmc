include containerd.inc

SRCREV = "0ac3cd1be170d180b2baed755e8f0da547ceb267"
SRC_URI = "git://github.com/docker/containerd.git;nobranch=1 \
          "
CONTAINERD_VERSION = "0.2.2"

PROVIDES += "virtual/containerd"
RPROVIDES_${PN} = "virtual/containerd"

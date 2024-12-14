require nanopb.inc

EXTRA_OECMAKE += " \
  -Dnanopb_PROTOC_PATH=/bin/false \
  -DBUILD_SHARED_LIBS=ON \
  -Dnanopb_BUILD_RUNTIME=ON \
  -Dnanopb_BUILD_GENERATOR=OFF \
  "

# Maintain compatability with old header locations for packages
# which haven't yet migrated to `nanopb/pb*.h`
do_install:append() {
  for hdr in ${D}${includedir}/nanopb/*; do
	ln -sv nanopb/$(basename "$hdr") ${D}${includedir}/
  done
}


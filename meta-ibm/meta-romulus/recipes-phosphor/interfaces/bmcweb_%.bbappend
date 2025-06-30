# Romulus doesn't have the space for the both zstd and xz compression
PACKAGECONFIG:remove = " \
    http-zstd \
"

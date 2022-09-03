# Include a backport kernel patch for TEE driver

SRC_URI:append = " \
    file://Add-sec_world_id-to-struct-tee_shm.patch \
    "

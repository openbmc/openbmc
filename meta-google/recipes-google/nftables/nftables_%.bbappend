# The meta-google recipe `nftables-systemd` provides this functionality for
# now so disable it in the nftables recipe.
PACKAGECONFIG:remove = "systemd"

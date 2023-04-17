# This is the default password for the OpenBMC root user account (0penBmc)
# Override salted and hashed value. The salted and hashed value are generated
# by command "openssl passwd -6 -salt rounds=1000\$UGMqyqdG 0penBmc"
AMPERE_DEFAULT_OPENBMC_PASSWORD = "'\$6\$rounds=1000\$UGMqyqdG\$uaE7HvA2vYhZYpIslelD1bsZMkXWV7YjL3wS2Vwj8rXyf90umESUzuR5if64N1LkNzTX.HUIi6D8s108y5GOB/'"
EXTRA_USERS_PARAMS:pn-obmc-phosphor-image = " \
  usermod -p ${AMPERE_DEFAULT_OPENBMC_PASSWORD} root; \
  "

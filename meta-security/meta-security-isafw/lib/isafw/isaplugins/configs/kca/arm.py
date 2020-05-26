############################################################################################
# Kernel Hardening Configurations
############################################################################################
hardening_kco = {'CONFIG_DEFAULT_MMAP_MIN_ADDR': 'not set',}
hardening_kco_ref = {'CONFIG_DEFAULT_MMAP_MIN_ADDR': '32768',}
############################################################################################
# Keys Kernel Configuration
############################################################################################
keys_kco = {}
keys_kco_ref = {}
############################################################################################
# Security Kernel Configuration
############################################################################################
security_kco = {'CONFIG_LSM_MMAP_MIN_ADDR': 'not set',}
security_kco_ref = {'CONFIG_LSM_MMAP_MIN_ADDR': '32768',}
############################################################################################
# Integrity Kernel Configuration
############################################################################################
integrity_kco = {}
integrity_kco_ref = {}
############################################################################################
# Comments
############################################################################################
comments = {'CONFIG_DEFAULT_MMAP_MIN_ADDR': 'Defines the portion of low virtual memory that should be protected from userspace allocation. Keeping a user from writing to low pages can help reduce the impact of kernel NULL pointer bugs.'}

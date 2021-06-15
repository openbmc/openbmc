############################################################################################
# Kernel Hardening Configurations
############################################################################################
hardening_kco = {'CONFIG_DEFAULT_MMAP_MIN_ADDR': 'not set',
                 'CONFIG_RANDOMIZE_BASE_MAX_OFFSET': 'not set',
                 'CONFIG_X86_INTEL_MPX': 'not set',
                 'CONFIG_X86_MSR': 'not set'
                 }
hardening_kco_ref = {'CONFIG_DEFAULT_MMAP_MIN_ADDR': '65536',  # x86 specific
                     'CONFIG_RANDOMIZE_BASE_MAX_OFFSET': '0x20000000,0x40000000',  # x86 specific
                     'CONFIG_X86_INTEL_MPX': 'y',  # x86 and certain HW variants specific
                     'CONFIG_X86_MSR': 'not set'
                     }
############################################################################################
# Keys Kernel Configuration
############################################################################################
keys_kco = {}
keys_kco_ref = {}
############################################################################################
# Security Kernel Configuration
############################################################################################
security_kco = {'CONFIG_LSM_MMAP_MIN_ADDR': 'not set',
                'CONFIG_INTEL_TXT': 'not set'}
security_kco_ref = {'CONFIG_LSM_MMAP_MIN_ADDR': '65536',  # x86 specific
                    'CONFIG_INTEL_TXT': 'y'}
############################################################################################
# Integrity Kernel Configuration
############################################################################################
integrity_kco = {}
integrity_kco_ref = {}
############################################################################################
# Comments
############################################################################################
comments = {'CONFIG_DEFAULT_MMAP_MIN_ADDR': 'Defines the portion of low virtual memory that should be protected from userspace allocation. Keeping a user from writing to low pages can help reduce the impact of kernel NULL pointer bugs.',
            'CONFIG_RANDOMIZE_BASE_MAX_OFFSET': 'Defines the maximal offset in bytes that will be applied to the kernel when kernel Address Space Layout Randomization (kASLR) is active.',
            'CONFIG_X86_INTEL_MPX': 'Enables MPX hardware features that can be used with compiler-instrumented code to check memory references. It is designed to detect buffer overflow or underflow bugs.',
            'CONFIG_X86_MSR': 'Enables privileged processes access to the x86 Model-Specific Registers (MSRs). MSR accesses are directed to a specific CPU on multi-processor systems. This alone does not provide security.'
            }

import os, struct

class NotELFFileError(Exception):
    pass

class ELFFile:
    EI_NIDENT = 16

    EI_CLASS      = 4
    EI_DATA       = 5
    EI_VERSION    = 6
    EI_OSABI      = 7
    EI_ABIVERSION = 8

    E_MACHINE    = 0x12

    # possible values for EI_CLASS
    ELFCLASSNONE = 0
    ELFCLASS32   = 1
    ELFCLASS64   = 2

    # possible value for EI_VERSION
    EV_CURRENT   = 1

    # possible values for EI_DATA
    ELFDATANONE  = 0
    ELFDATA2LSB  = 1
    ELFDATA2MSB  = 2

    PT_INTERP = 3

    def my_assert(self, expectation, result):
        if not expectation == result:
            #print "'%x','%x' %s" % (ord(expectation), ord(result), self.name)
            raise NotELFFileError("%s is not an ELF" % self.name)

    def __init__(self, name, bits = 0):
        self.name = name
        self.bits = bits
        self.objdump_output = {}

    def open(self):
        if not os.path.isfile(self.name):
            raise NotELFFileError("%s is not a normal file" % self.name)

        self.file = file(self.name, "r")
        # Read 4k which should cover most of the headers we're after
        self.data = self.file.read(4096)

        if len(self.data) < ELFFile.EI_NIDENT + 4:
            raise NotELFFileError("%s is not an ELF" % self.name)

        self.my_assert(self.data[0], chr(0x7f) )
        self.my_assert(self.data[1], 'E')
        self.my_assert(self.data[2], 'L')
        self.my_assert(self.data[3], 'F')
        if self.bits == 0:
            if self.data[ELFFile.EI_CLASS] == chr(ELFFile.ELFCLASS32):
                self.bits = 32
            elif self.data[ELFFile.EI_CLASS] == chr(ELFFile.ELFCLASS64):
                self.bits = 64
            else:
                # Not 32-bit or 64.. lets assert
                raise NotELFFileError("ELF but not 32 or 64 bit.")
        elif self.bits == 32:
            self.my_assert(self.data[ELFFile.EI_CLASS], chr(ELFFile.ELFCLASS32))
        elif self.bits == 64:
            self.my_assert(self.data[ELFFile.EI_CLASS], chr(ELFFile.ELFCLASS64))
        else:
            raise NotELFFileError("Must specify unknown, 32 or 64 bit size.")
        self.my_assert(self.data[ELFFile.EI_VERSION], chr(ELFFile.EV_CURRENT) )

        self.sex = self.data[ELFFile.EI_DATA]
        if self.sex == chr(ELFFile.ELFDATANONE):
            raise NotELFFileError("self.sex == ELFDATANONE")
        elif self.sex == chr(ELFFile.ELFDATA2LSB):
            self.sex = "<"
        elif self.sex == chr(ELFFile.ELFDATA2MSB):
            self.sex = ">"
        else:
            raise NotELFFileError("Unknown self.sex")

    def osAbi(self):
        return ord(self.data[ELFFile.EI_OSABI])

    def abiVersion(self):
        return ord(self.data[ELFFile.EI_ABIVERSION])

    def abiSize(self):
        return self.bits

    def isLittleEndian(self):
        return self.sex == "<"

    def isBigEndian(self):
        return self.sex == ">"

    def getShort(self, offset):
        return struct.unpack_from(self.sex+"H", self.data, offset)[0]

    def getWord(self, offset):
        return struct.unpack_from(self.sex+"i", self.data, offset)[0]

    def isDynamic(self):
        """
        Return True if there is a .interp segment (therefore dynamically
        linked), otherwise False (statically linked).
        """
        offset = self.getWord(self.bits == 32 and 0x1C or 0x20)
        size = self.getShort(self.bits == 32 and 0x2A or 0x36)
        count = self.getShort(self.bits == 32 and 0x2C or 0x38)

        for i in range(0, count):
            p_type = self.getWord(offset + i * size)
            if p_type == ELFFile.PT_INTERP:
                return True
        return False

    def machine(self):
        """
        We know the sex stored in self.sex and we
        know the position
        """
        return self.getShort(ELFFile.E_MACHINE)

    def run_objdump(self, cmd, d):
        import bb.process
        import sys

        if cmd in self.objdump_output:
            return self.objdump_output[cmd]

        objdump = d.getVar('OBJDUMP', True)

        env = os.environ.copy()
        env["LC_ALL"] = "C"
        env["PATH"] = d.getVar('PATH', True)

        try:
            bb.note("%s %s %s" % (objdump, cmd, self.name))
            self.objdump_output[cmd] = bb.process.run([objdump, cmd, self.name], env=env, shell=False)[0]
            return self.objdump_output[cmd]
        except Exception as e:
            bb.note("%s %s %s failed: %s" % (objdump, cmd, self.name, e))
            return ""

if __name__ == "__main__":
    import sys
    elf = ELFFile(sys.argv[1])
    elf.open()
    print elf.isDynamic()

MACHINEOVERRIDES =. "fb-fboss:"

require conf/machine/include/facebook.inc

# Darwin uses ttyS0 at 9600 baudrate
SERIAL_CONSOLES:facebook = "9600;ttyS0"

#!/bin/sh
#
# ImageMagick ptest:
# We ceate an BASERGB file for our usage using "magick"
# We convert this RGB file to BASEPNG
# Using BASEPNG we recreate RGB named REGENERATEDRGB
#
# BASERGB to BASEPNG to REGENERATEDRGB
# - Then compare  BASERGB with REGENERATEDRGB
#
# 0) The convert command is deprecated in IMv7
#    used "magick" instead of "convert"
# 1) We are checking if the binaries are present in RFS or not
# 2) We Created an RBG of size : WIDTH x HEIGHT pixels
# 3) Return value is captured after every major actio to capture the status
# 4) cmp -s is used to compare binary byte by byte data and
#    capture only exit status
# 5) Important parametsrs used are :
#    -depth                     : How many bits for each colour pixel
#    -alpha off  		: Don't consider transparency
#    -define png:color-type=2   : Make PNG work with truecolour RGB
#    -strip 			: Remove all non-pixel metadata og PNG
#                                 so file is reproducible
#    -set gamma 1.0             : No PNG brightness correction
#     gradient:red-blue         : Data moves liberly from RED to Blue vertically


WIDTH=16
HEIGHT=16
BASERGB=base.rgb
BASEPNG=base.png
REGENERATEDRGB=roundtrip.rgb

echo "[DEBUG] Starting ImageMagick Ptest with ${WIDTH}x${HEIGHT} pixels "

# Verify required binaries
for bin in magick cmp wc rm; do
    if [ -z "$(command -v "$bin" 2>/dev/null)" ]; then
        echo "[ERROR] Required binary '$bin' not found $PATH"
        exit 127
    fi
done


# Generate raw RGB
magick -size ${WIDTH}x${HEIGHT} gradient:red-blue \
    -depth 8 -type TrueColor \
    -alpha off -define png:color-type=2 \
    -strip -set gamma 1.0 \
    rgb:${BASERGB}

returnvalue=$?
if [ "$returnvalue" -ne 0 ]; then
    echo "[FAIL] Failed to generate RGB pattern "
    exit 1
else
    echo "[DEBUG] ${BASERGB} generated from gradient"
fi


# Convert raw RGB to PNG
magick -depth 8 -size ${WIDTH}x${HEIGHT} rgb:${BASERGB} \
    -type TrueColor -alpha off \
    -define png:color-type=2 -strip -set gamma 1.0 \
    ${BASEPNG}

returnvalue=$?
if [ $returnvalue -ne 0 ]; then
    echo "[FAIL] Failed to convert RGB to PNG"
    rm -f ${BASERGB}
    exit 1
else
    echo "[DEBUG] ${BASEPNG} generated from ${BASERGB}"
fi



# Regenerate raw RGB from PNG
magick ${BASEPNG} \
    -size ${WIDTH}x${HEIGHT} -depth 8 -type TrueColor \
    -alpha off -define png:color-type=2 \
    -strip -set gamma 1.0 \
    rgb:${REGENERATEDRGB}

returnvalue=$?
if [ $returnvalue -ne 0 ]; then
    echo "[FAIL] Failed to regenerate RGB from PNG"
    rm -f ${BASERGB} ${BASEPNG}
    exit 1
else
    echo "[DEBUG] ${REGENERATEDRGB} generated from ${BASEPNG}"
fi



# Compare original and recreated RGB
if cmp -s ${BASERGB} ${REGENERATEDRGB}; then
    echo "[PASS] RGB data identical after PNG round-trip"
    RESULT=0
else
    echo "[FAIL] RGB mismatch detected, printing their size "
    echo "[INFO] Base RGB size: $(wc -c < ${BASERGB}) bytes"
    echo "[INFO] Round-trip RGB size: $(wc -c < ${REGENERATEDRGB}) bytes"
    RESULT=1
fi



# Checking the identify tool from imagemagick to get the PNG metadata
# True is added in end to ensure that test script doesnt fail even if
# identify fails for any reason
echo "[DEBUG] PNG file info:"
identify -verbose ${BASEPNG} | grep -E "Depth|Type|Colorspace" || true



# Cleanup of files create by test code
echo "[DEBUG] Cleaning up temporary files"
rm -f ${BASERGB} ${BASEPNG} ${REGENERATEDRGB}
returnvalue=$?
echo "[DEBUG] Cleanup exit=$returnvalue"


# Logging the final result
if [ ${RESULT} -eq 0 ]; then
    echo "[DEBUG]: imagemagick-ptest.sh sucessfull "
else
    echo "[DEBUG]: imagemagick-ptest.sh failed "
fi


exit ${RESULT}

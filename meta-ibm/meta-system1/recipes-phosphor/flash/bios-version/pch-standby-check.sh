#!/bin/bash

# Find the GPIO pin associated with "pch-ready"
GPIO_PIN=$(gpiofind "pch-ready")

if [ -z "${GPIO_PIN}" ]; then
    echo "gpio 'pch-ready' not found in device tree. Exiting."
    exit 0
fi

# Extract gpiochip and line offset from the GPIO_PIN
GPIO_CHIP=$(echo "$GPIO_PIN" | cut -d' ' -f1)  # Extract gpiochip
GPIO_LINE=$(echo "$GPIO_PIN" | cut -d' ' -f2)  # Extract line offset

# Poll the GPIO value until it reads 1 (indicating power sequence completion)
while true; do
    GPIO_VALUE=$(gpioget "$GPIO_CHIP" "$GPIO_LINE")

    if [ "$GPIO_VALUE" -eq 1 ]; then
        echo "PCH Standby Power Sequence Complete"
        exit 0
    else
        echo "Waiting for PCH Standby Power Sequence..."
        sleep 5
    fi
done

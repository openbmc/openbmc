// SPDX-License-Identifier: Apache-2.0
// SPDX-FileCopyrightText: 2025 NVIDIA

#include <fcntl.h>
#include <gpiod.hpp>
#include <systemd/sd-daemon.h>

#include <chrono>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <filesystem>
#include <format>
#include <fstream>
#include <iostream>
#include <thread>
#include <unordered_map>

#include <config.h>

using namespace std::chrono_literals;

constexpr static const char *app_name = "platform_init";

// Map of GPIO name to line.  Holds lines open for the duration of the program
static std::unordered_map<std::string, gpiod::line> io;

void sleep_milliseconds(std::chrono::milliseconds milliseconds) {
  std::cerr << std::format("Sleeping for {} milliseconds\n",
                           milliseconds.count());
  std::this_thread::sleep_for(milliseconds);
}

void set_gpio(const char *line_name, int value,
              std::chrono::milliseconds find_timeout = 0ms) {
  std::cerr << std::format("{} Request to set to {}\n", line_name, value);
  std::chrono::milliseconds polling_time = 10ms;
  gpiod::line &line = io[line_name];
  if (!line) {
    do {
      line = gpiod::find_line(line_name);
      if (!line) {
        std::cerr << std::format("{} not found yet, waiting and retrying\n",
                                 line_name);

        sleep_milliseconds(polling_time);
        find_timeout -= polling_time;
      }
    } while (!line && find_timeout > 0s);
    if (!line && find_timeout <= 0s) {
      std::cerr << std::format("{} Unable to find\n", line_name);
      return;
    }
    try {
      line.request({app_name, gpiod::line_request::DIRECTION_OUTPUT, 0}, value);
    } catch (const std::system_error &e) {
      std::cerr << std::format("{} unable to set direction and value {}\n",
                               line_name, e.what());
      return;
    }
    // No need to set if the init did it for us
    std::cerr << std::format("{} Set to {}\n", line_name, value);
    return;
  }
  std::cerr << std::format("{} Settingto {}\n", line_name, value);
  line.set_value(value);
}

int get_gpio(const char *line_name) {
  std::cerr << std::format("{} Request to get\n", line_name);

  gpiod::line line = gpiod::find_line(line_name);
  if (!line) {
    std::cerr << std::format("{} Set unable to find\n", line_name);
    return -1;
  }
  try {
    line.request({app_name, gpiod::line_request::DIRECTION_INPUT, 0});
  } catch (const std::system_error &e) {
    std::cerr << std::format("{} unable to set {}\n", line_name, e.what());
  }

  int value = line.get_value();
  std::cerr << std::format("{} was {}\n", line_name, value);
  return value;
}

enum class GpioEventResult { Error, Asserted, Timeout };

struct GpioEvent {
  GpioEvent(const char *line_name_in, int value_in)
      : line_name(line_name_in), value(value_in) {
    line = gpiod::find_line(line_name);
    if (!line) {
      std::cerr << std::format("{} GpioEvent: Unable to find\n", line_name);
      return;
    }
    int edge = value ? ::gpiod::line_request::EVENT_RISING_EDGE
                     : ::gpiod::line_request::EVENT_FALLING_EDGE;

    line.request({app_name, edge, 0});

    int val = line.get_value();
    if (val == value) {
      std::cerr << std::format("{} GpioEvent is already {}\n", line_name, val);
    } else {
      std::cerr << std::format("GpioEvent created for {}\n", line_name);
    }
  }
  GpioEventResult wait() {
    if (!line) {
      std::cerr << std::format("Line {} wasn't initialized\n", line_name);
      return GpioEventResult::Error;
    }
    std::cerr << std::format("{}  Waiting to go to {}\n", line_name,
                             value ? "assert" : "deassert");
    auto events = line.event_wait(std::chrono::seconds(120));
    if (!events) {
      std::cerr << std::format("{} Timeout\n", line_name);
      return GpioEventResult::Timeout;
    }

    std::cerr << std::format("{} Asserted\n", line_name);

    return GpioEventResult::Asserted;
  }

  gpiod::line line;
  std::string line_name;
  int value;
};

void rebind_i2c(std::string number) {
  std::string bindpath =
      std::format("/sys/bus/platform/drivers/aspeed-i2c-bus/unbind", number);
  std::ofstream bindofs(bindpath);
  if (!bindofs) {
    std::cerr << std::format("{} unable to open\n", bindpath);
    return;
  }
  try {
    bindofs << std::format("{}.i2c\n", number);
  } catch (const std::system_error &e) {
    std::cerr << std::format("{} unable to write\n", bindpath);
    return;
  }
  bindofs.close();
  std::cerr << std::format("{} unbound\n", number);

  std::string unbindpath =
      std::format("/sys/bus/platform/drivers/aspeed-i2c-bus/bind", number);
  std::ofstream unbindofs(unbindpath);
  if (!unbindofs) {
    std::cerr << std::format("{} unable to open\n", unbindpath);
    return;
  }
  try {
    unbindofs << std::format("{}.i2c\n", number);
  } catch (const std::system_error &e) {
    std::cerr << std::format("{} unable to write\n", unbindpath);
    return;
  }
  std::cerr << std::format("{} bound\n", number);
}

void set_gpio_raw(int chip_num, int bit_num, int value) {
  std::string syspath = std::format("gpiochip{}", chip_num);
  std::cerr << std::format("Setting gpiochip{} bit {} to {}\n", chip_num,
                           bit_num, value);
  try {
    gpiod::chip chip(syspath);
    gpiod::line line = chip.get_line(bit_num);
    line.request({app_name, gpiod::line_request::DIRECTION_OUTPUT, 0}, value);
    std::cerr << std::format("gpiochip{} bit {} set to {}\n", chip_num, bit_num,
                             value);
  } catch (const std::system_error &e) {
    std::cerr << std::format("Error setting gpiochip{} bit {}: {}\n", chip_num,
                             bit_num, e.what());
  }
}

void new_device(unsigned int bus, unsigned int address,
                std::string_view device_type) {
  std::string path = std::format("/sys/bus/i2c/devices/i2c-{}/new_device", bus);
  std::cerr << std::format("attempting to open {}", path);
  std::ofstream new_device(path);
  if (!new_device) {
    std::cerr << "Error: Unable to create I2C device\n";
    return;
  }
  new_device << std::format("{} 0x{:02x}", device_type, address);
  new_device.close();

  std::cerr << std::format("{} device created at bus {}", device_type, bus);
}

void wait_for_path_to_exist(std::string_view path,
                            std::chrono::milliseconds timeout) {

  while (true) {
    std::error_code ec;
    bool exists = std::filesystem::exists(path, ec);
    if (exists) {
      return;
    }
    sleep_milliseconds(1ms);
    timeout -= 1ms;
  }
  std::cerr << std::format("Failed to wait for {} to exist", path);
}

void init_p2020_gpu_card() {
  std::cerr << "Initializing GPU card...\n";

  // Init the P2020 gpio expander
  new_device(14, 0x20, "pca6408");

  // Wait for device to be created
  auto device_path = "/sys/bus/i2c/devices/14-0020";
  wait_for_path_to_exist(device_path, 1000ms);

  // Find the GPIO chip number
  std::string gpio_chip;
  for (const auto &entry : std::filesystem::directory_iterator(device_path)) {
    std::string path = entry.path().string();
    if (path.find("gpiochip") != std::string::npos) {
      gpio_chip = path.substr(path.find("gpiochip") + std::strlen("gpiochip"));
      break;
    }
  }
  if (gpio_chip.empty()) {
    std::cerr << "Error: Could not find GPIO chip number\n";
    return;
  }

  std::cerr << "Found GPIO chip: gpiochip" << gpio_chip << "\n";
  unsigned int gpiochipint = 0;
  try {
    gpiochipint = std::stoi(gpio_chip);
  } catch (const std::exception &e) {
    std::cout << "Failed to convert gpiochip\n";
    return;
  }

  // Set MCU in recovery
  set_gpio_raw(gpiochipint, 3, 1);

  // Reset MCU
  set_gpio_raw(gpiochipint, 4, 0);
  set_gpio_raw(gpiochipint, 4, 1);

  // Switch MUX to MCU
  set_gpio_raw(gpiochipint, 5, 1);
}

int main() {
  // Reset USB hubs
  set_gpio("USB_HUB_RESET_L-O", 0, 10000ms);
  if (HMC_PRESENT != 1) {
    set_gpio("SEC_USB2_HUB_RST_L-O", 0, 10000ms);
  }

  sleep_milliseconds(100ms);

  if (HMC_PRESENT != 1) {
    set_gpio("SEC_USB2_HUB_RST_L-O", 1);
  }
  //  Write SGPIO_BMC_EN-O=1 to correctly set mux to send SGPIO signals to FPGA
  set_gpio("SGPIO_BMC_EN-O", 1);

  // Write the bit for BMC without HMC
  set_gpio("HMC_BMC_DETECT-O", HMC_PRESENT == 0, 30000ms);

  // Set BMC_EROT_FPGA_SPI_MUX_SEL-O = 1 to enable FPGA to access its EROT
  set_gpio("BMC_EROT_FPGA_SPI_MUX_SEL-O", 1);

  // Enable 12V
  set_gpio("BMC_12V_CTRL-O", 1, 10000ms);

  set_gpio("PWR_BRAKE_L-O", 1);
  set_gpio("SHDN_REQ_L-O", 1);
  set_gpio("SHDN_FORCE_L-O", 1);
  // Hold in reset (asserted) after standby power enabled
  set_gpio("SYS_RST_IN_L-O", 0);

  GpioEvent fpga_ready_wait = GpioEvent("FPGA_READY_BMC-I", 1);
  GpioEvent sec_erot_fpga_rst = GpioEvent("SEC_FPGA_READY_BMC-I", 1);

  // Release FPGA EROT from reset
  set_gpio("EROT_FPGA_RST_L-O", 1);
  set_gpio("SEC_EROT_FPGA_RST_L-O", 1);

  sleep_milliseconds(100ms);

  set_gpio("FPGA_RST_L-O", 1);

  if (fpga_ready_wait.wait() != GpioEventResult::Asserted) {
    std::cerr << "FPGA_READY_BMC-I failed to assert\n";
    // return EXIT_FAILURE;
  }

  if (sec_erot_fpga_rst.wait() != GpioEventResult::Asserted) {
    std::cerr << "SEC_FPGA_READY_BMC-I failed to assert\n";
    // return EXIT_FAILURE;
  }

  // ReInitialize the FPGA connected I2C buses to unstick them and let FruDevice
  // know it can scan for FRUs
  // I2c bus 1
  rebind_i2c("1e78a100");
  // I2c bus 2
  rebind_i2c("1e78a180");

  // Set sgpio signals
  set_gpio("RUN_POWER_EN-O", 1);
  set_gpio("SYS_RST_IN_L-O", 1);
  set_gpio("GLOBAL_WP_BMC-O", 0);

  set_gpio("BMC_READY-O", 1);

  if (INIT_CARD == 1) {
    init_p2020_gpu_card();
  }

  set_gpio("USB_HUB_RESET_L-O", 1);
  if (HMC_PRESENT != 1) {
    set_gpio("SEC_USB2_HUB_RST_L-O", 1);
  }

  sd_notify(0, "READY=1");
  std::cerr << "Platform init complete\n";
  pause();
  std::cerr << "Releasing platform\n";

  return EXIT_SUCCESS;
}

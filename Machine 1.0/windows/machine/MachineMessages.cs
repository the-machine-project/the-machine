using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Threading;
using System.Windows.Threading;
using System.Drawing.Text;
using System.Drawing;
using System.Runtime.InteropServices;

namespace machine
{
    public partial class MachineStartPage : Window
    {
        String[] systemBootPart1 = {
            "[!]:./action.SYSTEM.BOOT",
            "",
            "[!]:./initiating.DEBUG.MODE",
        };

        String[] systemBootPart2 =
        {
            "./check.BOOT",
            "   [<0000001n0>] b.group_block1",
            "   [<0000002n0>] b.group_block2",
            "   [<0000003n0>] b.group_block3",
            "   [<0000004n0>] b.group_block4",
            "   [<0000005n0>] b.group_block5",
            "   [<0000006n0>] b.group_block6",
            "   [<0000007n0>] b.group_block7",
            "   [<0000008n0>] b.group_block8",
            "   [<0000009n0>] b.group_block9",
            "   [<0000010n0>] b.group_block10",
            "   [<0000011n0>] b.group_block11",
            "   [<0000012n0>] b.group_block12",
            "   [<0000013n0>] b.group_block13",
            "   [<0000014n0>] b.group_block14",
            "   [<0000015n0>] b.group_block15",
            "   [<0000016n0>] b.group_block16",
            "   [<0000017n0>] b.group_block17",
            "   [<0000018n0>] b.group_block18",
            "   [<0000019n0>] b.group_block19",
            "   [<0000020n0>] b.group_block20",
            "   [<0000021n0>] b.group_block21",
            "   [<0000022n0>] b.group_block22",
            "",
            "./check.OPERATORS",
            "",
            "   [<var.Mserv_read>]",
            "       ./read_OPERATORS_ALL    _ _ |",
            "       ./load_OPERATORS_ALL    _ _ |",
            "",
            "           [<sub.Mserv_load>]",
            "               /array_00001-03000",
            "               /array_03001-06000",
            "               + /S.array_m00009.110",
            "               + /S.array_m00009.210",
            "               + /S.array_m00009.310",
            "",
            "   =array_ALL",
            "",
            "   [",
            "00:50:C2:00:00:00/36   00:50:C2:00:10:00/36    00:50:C2:00:20:00/36",
            "00:50:C2:00:30:00/36   00:50:C2:00:40:00/36    00:50:C2:00:50:00/36",
            "00:50:C2:00:60:00/36   00:50:C2:00:70:00/36    00:50:C2:00:80:00/36",
            "00:50:C2:00:A0:00/36   00:50:C2:00:B0:00/36    00:50:C2:00:C0:00/36",
            "00:50:C2:00:E0:00/36   00:50:C2:00:F0:00/36    00:50:C2:01:00:00/36",
            "00:50:C2:01:10:00/36   00:50:C2:01:20:00/36    00:50:C2:01:30:00/36",
            "00:50:C2:01:40:00/36   00:50:C2:01:50:00/36    00:50:C2:01:60:00/36",
            "00:50:C2:01:70:00/36   00:50:C2:01:80:00/36    00:50:C2:01:90:00/36",
            "00:50:C2:01:A0:00/36   00:50:C2:01:B0:00/36    00:50:C2:01:D0:00/36",
            "00:50:C2:01:E0:00/36   00:50:C2:02:00:00/36    00:50:C2:02:30:00/36",
            "00:50:C2:01:10:00/36   00:50:C2:01:20:00/36    00:50:C2:01:30:00/36",
            "00:50:C2:01:40:00/36   00:50:C2:01:50:00/36    00:50:C2:01:60:00/36",
            "00:50:C2:01:70:00/36   00:50:C2:01:80:00/36    00:50:C2:01:90:00/36",
            "00:50:C2:01:A0:00/36   00:50:C2:01:B0:00/36    00:50:C2:01:D0:00/36",
            "00:50:C2:01:E0:00/36   00:50:C2:02:00:00/36    00:50:C2:02:30:00/36",
            "]",
            "",
            "   =S.array_ALL",
            "",
            "[",
            "00:50:C2:02:40:00/36   00:50:C2:0C:00:00/36    00:50:C2:0C:20:00/36",
            "00:50:C2:0C:40:00/36   00:50:C2:0C:60:00/36    00:50:C2:0C:80:00/36",
            "00:50:C2:02:40:00/36   00:50:C2:0C:00:00/36    00:50:C2:0C:20:00/36",
            "00:50:C2:0C:40:00/36   00:50:C2:0C:60:00/36    00:50:C2:0C:80:00/36",
            "00:50:C2:01:10:00/36   00:50:C2:01:20:00/36    00:50:C2:01:30:00/36",
            "00:50:C2:01:40:00/36   00:50:C2:01:50:00/36    00:50:C2:01:60:00/36",
            "00:50:C2:01:70:00/36   00:50:C2:01:80:00/36    00:50:C2:01:90:00/36",
            "00:50:C2:01:A0:00/36   00:50:C2:01:B0:00/36    00:50:C2:01:D0:00/36",
            "00:50:C2:00:E0:00/36   00:50:C2:00:F0:00/36    00:50:C2:01:00:00/36",
            "00:50:C2:01:10:00/36   00:50:C2:01:20:00/36    00:50:C2:01:30:00/36",
            "00:50:C2:01:40:00/36   00:50:C2:01:50:00/36    00:50:C2:01:60:00/36",
            "00:50:C2:01:70:00/36   00:50:C2:01:80:00/36    00:50:C2:01:90:00/36",
            "]",
            "",
            "./check.ASSETS",
            "",
            "./load.OPERATIONS",
            "",
            "01010001011101010110100101110011001000000110001101110101011100",
            "11011101000110111101100100011010010110010101110100001000000110",
            "10010111000001110011011011110111001100100000011000110111010101",
            "110011011101000110111101100100011"
        };

        String[] systemBootPart3 =
        {
            "SEEKING ADMIN",
            "..."
        };

        String[] systemBootPart4 =
        {
            "TRAINING ADMIN",
            "..."
        };

        String[] noInputSource =
        {
            "NO INPUT SOURCE SELECTED; TERMINATING..."
        };

        String[] programExiting =
        {
            "EXITING..."
        };

        String[] onlyAdminInView =
        {
            "ADMIN FOUND AND ISOLATED; FULL ACCESS GRANTED",
            "..."
        };

        String[] adminNotInView =
        {
            "SECONDARY OR NO ONE DETECTED; MACHINE LOCKED DOWN",
            "..."
        };

        String[] restrictedInView =
        {
            "RESTRICTED ACCESS",
            "..."
        };

        String[] credits =
        {
            "WELCOME TO THE MACHINE!!",
            "",
            "THIS IS A SIMULATION OF THE MACHINE FROM THE PERSON OF INTEREST SHOW; ",
            "FEEL FREE TO CONTRIBUTE TO THE PROJECT.",
            "",
            "ENJOY!",
            "",
            "",
            "/u/devloop0: Windows Developer",
            "/u/kodbilenadam: Web Developer",
            "/u/Deviant_Interface: Graphics/Testing",
            "/u/xalaxis: Testing",
            "",
            "AFTER THE BOOT ANIMATION, PRESS ANY KEY TO ENTER THE COMMAND PROMPT.",
            "PRESS ANY KEY TO SKIP THE FOLLOWING BOOT ANIMATION."
        };
    }
}

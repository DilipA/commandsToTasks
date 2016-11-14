import os
import sys


if __name__ =="__main__":
    args = sys.argv
    directory = args[1]

    if directory == "L0":
        for g in os.listdir(directory):
            l = []
            with open(os.path.join(directory, g), 'r') as f:
                for line in f:
                    l.append(line.strip())

            with open(os.path.join(directory, g), 'w') as f:
                f.write(l[0] + "\n")
                f.write("west" + "\n")
                f.write(" ".join(l[2:]) + "\n")

    elif directory == "L1" or directory == "L2":
        for g in os.listdir(directory):
            l = []
            with open(os.path.join(directory, g), 'r') as f:
                for line in f:
                    l.append(line.strip())

            with open(os.path.join(directory, g), 'w') as f:
                f.write(l[0] + "\n")
                f.write("agentToRoom room1" + "\n")
                f.write(" ".join(l[2:]) + "\n")

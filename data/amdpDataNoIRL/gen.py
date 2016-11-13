import os
import sys


if __name__ =="__main__":
    args = sys.argv
    directory = args[1]

    for g in os.listdir(directory):
        with open(os.path.join(directory, g), 'a+') as f:
            f.write("agentToDoor door0\n")
            f.write("room,3,door1,door0\n")
            f.write("room,1,door1 room,0,door0\n")
            f.write("door,room2,room0\n")
            f.write("door,room1,room0\n")
            f.write("block,4,1,room0 agent,room2\n")

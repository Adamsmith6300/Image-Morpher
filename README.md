# Image-Morpher

An Android application that allows the user to select 2 different images and then morphs the 2 images together using the Beier-Neely morphing algorithm. Users can draw and move the line pairs used for the warping aspect of the algorithm. The user can also select how many intermediate frames to create for the morphing animation. The results can then be saved locally to the device and opened later to view the animation again.

As you increase the number of line pairs the program takes longer to process. It is for this reason that I implemented multi-threading to speed up the algorithm. 

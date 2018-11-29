package com.smart.pen.core.model;
/**
 * 
 * @author Xiaoz
 * @date August 7, 2015 10:29:20 AM
 *
 * Description
 */
public class FrameSizeObject {
	//Display the area size of the canvas
	/**
	 * Display canvas area width
	 */
	public int frameWidth;
	/**
	 * Show canvas area high
	 */
	public int frameHeight;
	
	
	//Paper size
	/**
	 * Paper writable area wide
	 */
	public int sceneWidth;
	/**
	 * Paper writable area is high
	 */
	public int sceneHeight;
	

	/**
	 * Display writable area width
	 */
	public int windowWidth;
	
	/**
	 * Show high writable area
	 */
	public int windowHeight;
	public int windowLeft;
	public int windowTop;
	
	/**
	 * Zoom width
	 */
	public int zoomWidth;
	
	/**
	 * Zoom high
	 */
	public int zoomHeight;
	
	
	/**
	 * Set the canvas writable area size
	 * @return
	 */
	public void initWindowSize(){
		if(sceneWidth > sceneHeight){
			windowWidth = frameWidth;
			windowHeight = (int)((float)sceneHeight * ((float)frameWidth / (float)sceneWidth));
			
			if(windowHeight > frameHeight){
				windowWidth = (int)((float)windowWidth * ((float)frameHeight / (float)windowHeight));
				windowHeight =  frameHeight;

				windowTop = 0;
				windowLeft = (frameWidth - windowWidth) / 2;
			}else{
				windowLeft = 0;
				windowTop = (frameHeight - windowHeight) / 2;
			}
		}else{
			windowHeight = frameHeight;
			windowWidth = (int)((float)sceneWidth * ((float)windowHeight / (float)sceneHeight));
			
			if(windowWidth > frameWidth){
				windowHeight = (int)((float)windowHeight * ((float)frameWidth / (float)windowWidth));
				windowWidth = frameWidth;
				
				windowLeft = 0;
				windowTop = (frameHeight - windowHeight) / 2;
			}else{
				windowTop = 0;
				windowLeft = (frameWidth - windowWidth) / 2;
			}
		}
	}
	
	
	/**
	 * Set the window zoom size, you need to execute after initWindowSize
	 * @param refSize
	 * @return
	 */
	public void setWindowZoomSize(int refSize){
		zoomWidth = windowWidth;
		zoomHeight = windowHeight;
	
	    if(windowWidth > windowHeight){
	        if(windowHeight > refSize){
	        	zoomHeight = refSize;
	        	zoomWidth = (int)((float)refSize / (float)windowHeight * windowWidth);
	        }
	    }else{
	        if(windowWidth > refSize){
	        	zoomWidth = refSize;
	        	zoomHeight = (int)((float)refSize / (float)windowWidth * windowHeight);
	        }
	    }
	}
}

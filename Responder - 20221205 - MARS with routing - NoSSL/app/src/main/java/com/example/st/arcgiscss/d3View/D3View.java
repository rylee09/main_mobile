
package com.example.st.arcgiscss.d3View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Optional
 *@D3View TextView textView   member variable named xml id name, no event, leave blank
 *@D3View(id="textView") TextView textView;  //does not correspond to xml id, custom ID
 *@D3View(click="onClick") TextView textView;    //click="onClick" corresponds to a public method onClick in the activity, which will be called directly
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface D3View {
	public int id() default 0;
	public String click() default "";
	public String longClick() default "";
	public String itemClick() default "";
	public String itemLongClick() default "";
	public String focusChange() default "";
}
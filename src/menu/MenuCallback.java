package menu;

import java.util.EventListener;

/**
 * @brief A menu callback interface for invoking methods.
 * 
 * @author David MacDermot
 *
 * @date 02-07-2012
 * 
 * @bug
 */
public interface MenuCallback extends EventListener {
	public void Invoke(); 
}

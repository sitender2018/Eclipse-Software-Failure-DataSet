package org.eclipse.ui.texteditor;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */


import java.util.HashMap;import java.util.Map;import java.util.ResourceBundle;import org.eclipse.core.resources.IResource;import org.eclipse.core.runtime.CoreException;import org.eclipse.core.runtime.IAdaptable;import org.eclipse.core.runtime.Platform;import org.eclipse.swt.widgets.Shell;import org.eclipse.jface.dialogs.ErrorDialog;import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;import org.eclipse.jface.text.BadLocationException;import org.eclipse.jface.text.IDocument;import org.eclipse.jface.text.ITextSelection;import org.eclipse.jface.window.Window;import org.eclipse.ui.IEditorInput;import org.eclipse.ui.PlatformUI;



/**
 * Action for creating a marker of a specified type for the editor's
 * input element based on the editor's selection. If required, the 
 * action asks the user to provide a marker label. The action is initially
 * associated with a text editor via the constructor, but that can be 
 * subsequently changed using <code>setEditor</code>.
 * <p>
 * The following keys, prepended by the given option prefix,
 * are used for retrieving resources from the given bundle:
 * <ul>
 *   <li><code>"dialog.title"</code> - the input dialog's title</li>
 *   <li><code>"dialog.message"</code> - the input dialog's message</li>
 *   <li><code>"error.dialog.title"</code> - the error dialog's title</li>
 *   <li><code>"error.dialog.message"</code> - the error dialog's message</li>
 * </ul>
 * This class may be instantiated but is not intended for subclassing.
 * </p>
 */
public class AddMarkerAction extends TextEditorAction {

	
	/** The type for newly created markers */
	private String fMarkerType;
	/** Should the user be asked for a label? */
	private boolean fAskForLabel;
	/** The action's resource bundle */
	private ResourceBundle fBundle;
	/** The prefix used for resource bundle lookup */
	private String fPrefix;

	
	/**
	 * Creates a new action for the given text editor. The action configures its
	 * visual representation from the given resource bundle.
	 *
	 * @param bundle the resource bundle
	 * @param prefix a prefix to be prepended to the various resource keys
	 *   (described in <code>ResourceAction</code> constructor), or 
	 *   <code>null</code> if none
	 * @param editor the text editor
	 * @param markerType the type of marker to add
	 * @param askForLabel <code>true</code> if the user should be asked for 
	 *   a label for the new marker
	 * @see ResourceAction#ResourceAction
	 */
	public AddMarkerAction(ResourceBundle bundle, String prefix, ITextEditor textEditor, String markerType, boolean askForLabel) {
		super(bundle, prefix, textEditor);
		fBundle= bundle;
		fPrefix= prefix;
		fMarkerType= markerType;
		fAskForLabel= askForLabel;
	}
	
	/**
	 * Returns this action's resource bundle.
	 *
	 * @return this action's resource bundle
	 */
	protected ResourceBundle getResourceBundle() {
		return fBundle;
	}
	
	/**
	 * Returns this action's resource key prefix.
	 *
	 * @return this action's resource key prefix
	 */
	protected String getResourceKeyPrefix() {
		return fPrefix;
	}
	
	/*
	 * @see IAction#run()
	 */
	public void run() {
		IResource resource= getResource();
		if (resource == null)
			return;
		Map attributes= getInitialAttributes();
		if (fAskForLabel) {
			if (!askForLabel(attributes))
				return;
		}
		
		try {
			MarkerUtilities.createMarker(resource, attributes, fMarkerType);
		} catch (CoreException x) {
			
			Platform.getPlugin(PlatformUI.PLUGIN_ID).getLog().log(x.getStatus());
			
			Shell shell= getTextEditor().getSite().getShell();
			String title= getString(fBundle, fPrefix + "error.dialog.title", fPrefix + "error.dialog.title"); //$NON-NLS-2$ //$NON-NLS-1$
			String msg= getString(fBundle, fPrefix + "error.dialog.message", fPrefix + "error.dialog.message"); //$NON-NLS-2$ //$NON-NLS-1$
			
			ErrorDialog.openError(shell, title, msg, x.getStatus());
		}
	}
	
	/*
	 * @see TextEditorAction#update()
	 */
	public void update() {
		setEnabled(true);
	}
	
	/**
	 * Asks the user for a marker label. Returns <code>true</code> if a label
	 * is entered, <code>false</code> if the user cancels the input dialog.
	 * The value for the attribute <code>message</code> is modified in the given
	 * attribute map.
	 *
	 * @param attributes the attributes map
	 * @return <code>true</code> if a label has been entered
	 */
	protected boolean askForLabel(Map attributes) {
		
		Object o= attributes.get("message"); //$NON-NLS-1$
		String proposal= (o instanceof String) ? (String) o : ""; //$NON-NLS-1$
		if (proposal == null)
			proposal= ""; //$NON-NLS-1$
			
		String title= getString(fBundle, fPrefix + "dialog.title", fPrefix + "dialog.title"); //$NON-NLS-2$ //$NON-NLS-1$
		String message= getString(fBundle, fPrefix + "dialog.message", fPrefix + "dialog.message"); //$NON-NLS-2$ //$NON-NLS-1$
		IInputValidator inputValidator = new IInputValidator() {
			public String isValid(String newText) {
				return  (newText == null || newText.length() == 0) ? " " : null;  //$NON-NLS-1$
			}
		};		
		InputDialog dialog= new InputDialog(getTextEditor().getSite().getShell(), title, message, proposal, inputValidator);
		
		String label= null;
		if (dialog.open() != Window.CANCEL)
			label= dialog.getValue();
			
		if (label == null)
			return false;
			
		label= label.trim();
		if (label.length() == 0)
			return false;
			
		attributes.put("message", label); //$NON-NLS-1$
		return true;
	}
	
	/**
	 * Returns the attributes the new marker will be initialized with.
	 * Subclasses may extend or replace this method.
	 *
	 * @return the attributes the new marker will be initialized with
	 */
	protected Map getInitialAttributes() {
		
		Map attributes= new HashMap(11);
		
		ITextSelection selection= (ITextSelection) getTextEditor().getSelectionProvider().getSelection();
		if (!selection.isEmpty()) {

			int start= selection.getOffset();
			int length= selection.getLength();

			if (length < 0) {
				length= -length;
				start -= length;
			}

			MarkerUtilities.setCharStart(attributes, start);
			MarkerUtilities.setCharEnd(attributes, start + length);
						
			// marker line numbers are 1-based
			int line= selection.getStartLine();
			MarkerUtilities.setLineNumber(attributes, line == -1 ? -1 : line + 1);

			IDocument document= getTextEditor().getDocumentProvider().getDocument(getTextEditor().getEditorInput());
			MarkerUtilities.setMessage(attributes, getLabelProposal(document, start, length));

		}
		
		return attributes;
	}
	
	/**
	 * Returns the initial label for the marker.
	 *
	 * @param document the document from which to extract a label proposal
	 * @param offset the document offset of the range from which to extract the label proposal
	 * @param length the length of the range from which to extract the label proposal
	 * @return the label proposal
	 */
	protected String getLabelProposal(IDocument document, int offset, int length) {
		
		
		try {
			
			
			if (length > 0)
				return document.get(offset, length);
				
			
			char ch;

			// Get the first white char before the selection.
			int left= offset;

			int line= document.getLineOfOffset(offset);
			int limit= document.getLineOffset(line);

			while (left > limit) {
				ch= document.getChar(left);
				if (Character.isWhitespace(ch))
					break;
				--left;
			}

			limit += document.getLineLength(line);

			// Now get the first letter.
			while (left <= limit) {
				ch= document.getChar(left);
				if (!Character.isWhitespace(ch))
					break;
				++left;
			}

			if (left > limit)
				return null;

			// Get the next white char.
			int right= (offset + length > limit ? limit : offset + length);
			while (right < limit) {
				ch= document.getChar(right);
				if (Character.isWhitespace(ch))
					break;
				++right;
			}

			// Trim the string and return it.
			if (left != right) {
				String label= document.get(left, right - left);
				return label.trim();
			}

		} catch (BadLocationException x) {
			// don't proposal label then
		}

		return null;
	}
	
	/** 
	 * Returns the resource on which to create the marker, 
	 * or <code>null</code> if there is no applicable resource. This
	 * queries the editor's input using <code>getAdapter(IResource.class)</code>.
	 * Subclasses may override this method.
	 *
	 * @return the resource to which to attach the newly created marker
	 */
	protected IResource getResource() {
		IEditorInput input= getTextEditor().getEditorInput();
		return (IResource) ((IAdaptable) input).getAdapter(IResource.class);
	}
}

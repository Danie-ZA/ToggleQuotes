package ToggleQuotes.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.command.WriteCommandAction

class ToggleQuotesAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val replacedText: String;
        val project = e.project ?: return
        val editor = e.getData(com.intellij.openapi.actionSystem.PlatformDataKeys.EDITOR) ?: return

        // Get the selected text in the editor
        val selectedText = editor.selectionModel.selectedText ?: return

        // Check if selected text contains double quotes
        if (selectedText.contains("\"")) {
            // Perform character replacement (replace " with ')
            replacedText = selectedText.replace('\"', '\'');
        } else {
            // Perform character replacement (replace ' with ")
             replacedText = selectedText.replace('\'', '\"');
        }

        // Replace the selected text in the editor
        WriteCommandAction.runWriteCommandAction(project) {
            editor.getDocument().replaceString(
                editor.getSelectionModel().getSelectionStart(),
                editor.getSelectionModel().getSelectionEnd(),
                replacedText
            );
        }
    }
}
import { Injectable } from '@angular/core';
import { NotFilledDialogComponent } from "../shared/not-filled-dialog/not-filled-dialog.component";
import { MatDialog } from "@angular/material/dialog";

@Injectable({
    providedIn: 'root'
})
export class DialogService {
    openNotFilledDialog(notFilled: MatDialog, paragraphContent: string, dialogListItems: Array<string>): void {
        const dialog = notFilled.open(NotFilledDialogComponent,{
            data: {
                paragraphContent : paragraphContent,
                listItems : dialogListItems
            }
        });
        dialog.componentInstance.closeEvent.subscribe(() => dialog.close());
    }
}
namespace machine
{
    partial class InputSelection
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.PopupSelection = new System.Windows.Forms.Panel();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.cameraNumberInput = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.selectSource = new System.Windows.Forms.Button();
            this.fileSelection = new System.Windows.Forms.RadioButton();
            this.webcamSelection = new System.Windows.Forms.RadioButton();
            this.cameraFrameRateInput = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.resourceDisposalInput = new System.Windows.Forms.CheckBox();
            this.PopupSelection.SuspendLayout();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // PopupSelection
            // 
            this.PopupSelection.Controls.Add(this.groupBox1);
            this.PopupSelection.Dock = System.Windows.Forms.DockStyle.Fill;
            this.PopupSelection.Location = new System.Drawing.Point(0, 0);
            this.PopupSelection.Name = "PopupSelection";
            this.PopupSelection.Size = new System.Drawing.Size(284, 261);
            this.PopupSelection.TabIndex = 0;
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.resourceDisposalInput);
            this.groupBox1.Controls.Add(this.label2);
            this.groupBox1.Controls.Add(this.cameraFrameRateInput);
            this.groupBox1.Controls.Add(this.cameraNumberInput);
            this.groupBox1.Controls.Add(this.label1);
            this.groupBox1.Controls.Add(this.selectSource);
            this.groupBox1.Controls.Add(this.fileSelection);
            this.groupBox1.Controls.Add(this.webcamSelection);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(260, 193);
            this.groupBox1.TabIndex = 2;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Input Source Properties";
            // 
            // cameraNumberInput
            // 
            this.cameraNumberInput.Location = new System.Drawing.Point(112, 80);
            this.cameraNumberInput.Name = "cameraNumberInput";
            this.cameraNumberInput.Size = new System.Drawing.Size(121, 20);
            this.cameraNumberInput.TabIndex = 4;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(20, 83);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(86, 13);
            this.label1.TabIndex = 3;
            this.label1.Text = "Camera Number:";
            // 
            // selectSource
            // 
            this.selectSource.Location = new System.Drawing.Point(169, 159);
            this.selectSource.Name = "selectSource";
            this.selectSource.Size = new System.Drawing.Size(75, 23);
            this.selectSource.TabIndex = 2;
            this.selectSource.Text = "Select";
            this.selectSource.UseVisualStyleBackColor = true;
            this.selectSource.Click += new System.EventHandler(this.selectSource_Click);
            // 
            // fileSelection
            // 
            this.fileSelection.AutoSize = true;
            this.fileSelection.Location = new System.Drawing.Point(23, 27);
            this.fileSelection.Name = "fileSelection";
            this.fileSelection.Size = new System.Drawing.Size(41, 17);
            this.fileSelection.TabIndex = 0;
            this.fileSelection.TabStop = true;
            this.fileSelection.Text = "File";
            this.fileSelection.UseVisualStyleBackColor = true;
            // 
            // webcamSelection
            // 
            this.webcamSelection.AutoSize = true;
            this.webcamSelection.Location = new System.Drawing.Point(23, 50);
            this.webcamSelection.Name = "webcamSelection";
            this.webcamSelection.Size = new System.Drawing.Size(68, 17);
            this.webcamSelection.TabIndex = 1;
            this.webcamSelection.TabStop = true;
            this.webcamSelection.Text = "Webcam";
            this.webcamSelection.UseVisualStyleBackColor = true;
            // 
            // cameraFrameRateInput
            // 
            this.cameraFrameRateInput.Location = new System.Drawing.Point(130, 107);
            this.cameraFrameRateInput.Name = "cameraFrameRateInput";
            this.cameraFrameRateInput.Size = new System.Drawing.Size(103, 20);
            this.cameraFrameRateInput.TabIndex = 5;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(20, 110);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(104, 13);
            this.label2.TabIndex = 6;
            this.label2.Text = "Camera Frame Rate:";
            // 
            // resourceDisposalInput
            // 
            this.resourceDisposalInput.AutoSize = true;
            this.resourceDisposalInput.Location = new System.Drawing.Point(23, 133);
            this.resourceDisposalInput.Name = "resourceDisposalInput";
            this.resourceDisposalInput.Size = new System.Drawing.Size(115, 17);
            this.resourceDisposalInput.TabIndex = 7;
            this.resourceDisposalInput.Text = "Resource Disposal";
            this.resourceDisposalInput.UseVisualStyleBackColor = true;
            // 
            // InputSelection
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(284, 261);
            this.Controls.Add(this.PopupSelection);
            this.Name = "InputSelection";
            this.Text = "InputSelection";
            this.PopupSelection.ResumeLayout(false);
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel PopupSelection;
        private System.Windows.Forms.RadioButton webcamSelection;
        private System.Windows.Forms.RadioButton fileSelection;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.Button selectSource;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox cameraNumberInput;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TextBox cameraFrameRateInput;
        private System.Windows.Forms.CheckBox resourceDisposalInput;
    }
}
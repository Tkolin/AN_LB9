package com.example.an_lb9;

import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
public class TextEditorFragment extends Fragment {
    private EditText editText;
    private Button buttonSave;
    private Button buttonCreateNew;
    private Button buttonOpenFile;
    private File openedfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_editor, container, false);

        editText = view.findViewById(R.id.editText);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCreateNew = view.findViewById(R.id.buttonCreateNew);
        buttonOpenFile = view.findViewById(R.id.buttonOpenFile);

        // Обработчик ввода
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ничего не делаем
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Вызываем метод для сохранения текста при каждом изменении
                saveTextToFile();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Ничего не делаем
            }
        });

        // Обработчики нажатия на кнопки
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTextToFile();
            }
        });

        buttonCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewFile();
            }
        });

        buttonOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileListDialog();
            }
        });

        // Загрузка текста из файла при запуске фрагмента
        openedfile = new File(getContext().getFilesDir(),"my_file.txt" );
        loadTextFromFile();
        return view;
    }

    // Метод для сохранения текста в файле
    private void saveTextToFile() {
        String text = editText.getText().toString();
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(openedfile);
            fileOutputStream.write(text.getBytes());
            Toast.makeText(getContext(), "Текст сохранен", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка при сохранении текста", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для создания нового файла с пользовательским именем
    private void createNewFile() {
        // Создание диалогового окна для ввода имени файла
        final EditText input = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Введите имя нового файла");
        builder.setView(input);

        builder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = input.getText().toString();
                if (!fileName.isEmpty()) {
                    openedfile = new File(getContext().getFilesDir(), fileName);
                    Toast.makeText(getContext(), "Файл создан: " + fileName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Введите имя файла", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Метод для отображения списка доступных файлов и выбора файла для открытия
    private void showFileListDialog() {
        File directory = getContext().getFilesDir();
        File[] files = directory.listFiles();

        if (files == null || files.length == 0) {
            Toast.makeText(getContext(), "Нет доступных файлов", Toast.LENGTH_SHORT).show();
            return;
        }

        final CharSequence[] fileNames = new CharSequence[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Выберите файл для открытия");
        builder.setItems(fileNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = fileNames[which].toString();
                openedfile = new File(getContext().getFilesDir(), fileName);
                loadTextFromFile();
                Toast.makeText(getContext(), "Файл открыт: " + fileName, Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    // Метод для загрузки текста из выбранного файла
    private void loadTextFromFile() {
        if (openedfile.exists()) {
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(openedfile));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line).append('\n');
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Ошибка при загрузке текста", Toast.LENGTH_SHORT).show();
            }

            editText.setText(text.toString());
        }
    }
}

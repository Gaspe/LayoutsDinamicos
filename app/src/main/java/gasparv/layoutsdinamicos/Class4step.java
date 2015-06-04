package gasparv.layoutsdinamicos;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GasparV on 27/05/2015.
 */
public class Class4step {
    public int step_id;
    public long proc_id;
    public Contenido Contenido;

    public Class4step() {
    }

    public static class Contenido {
        Campo[] campos; Decision[] Decisiones;
        public Contenido(Campo[] campos, Class4step.Decision[] Decis) {
            this.campos = campos; Decisiones = Decis;}
    }

    public static class Campo {
        public long Campo_id; public String titulo; public Tipo_paso tipo_campo; public String[] VPosibleCampo;
        public Campo(long Campo_id, String titulo, Tipo_paso tipo_campo, String[] Valores_Posibles) {
            this.Campo_id = Campo_id;this.titulo = titulo;this.tipo_campo = tipo_campo;VPosibleCampo = Valores_Posibles;
        }
    }

    public static class Decision {
        Branch[] Branches;
        public long ir_a_paso;
        public Decision(Branch[] branches, long ir_a_paso) {
            Branches = branches;
            this.ir_a_paso = ir_a_paso;
        }
    }

    public static class Branch {
        public long Campo_id;
        public Tipo_comparacion tcomparacion;
        public String Value;
        public Branch(long Campo_id, Tipo_comparacion tcomparacion, String value) {
            this.Campo_id = Campo_id;
            this.tcomparacion = tcomparacion;
            Value = value;
        }
    }

    public enum Tipo_paso {
        Pregunta,
        Si_o_No,
        Numerico,
        Texto
    }

    public enum Tipo_comparacion {
        Mayor_Que, Menor_Que, Igual
    }
    public static Class4step getStep(JSONObject step_json) {
        Class4step step = new Class4step();
        try {
            step.step_id = step_json.getInt("step_id");
            step.proc_id = step_json.getLong("procedure_id");
            String Contenido = step_json.getString("content");
            //Contenido = Html.fromHtml(Contenido).toString();
            JSONObject Contenido_json = new JSONObject(Contenido);
            JSONArray Campos = Contenido_json.getJSONArray("Fields");
            Campo[] Campos_array = new Campo[Campos.length()];
            for (int i = 0; i < Campos.length(); i++) {
                JSONObject obj = Campos.getJSONObject(i);
                String values[] = null;
                try {
                    JSONArray values_array = obj.getJSONArray("possible_values");
                    values = new String[values_array.length()];
                    for (int j = 0; j < values_array.length(); j++) {
                        values[j] = values_array.getString(j);
                    }
                } catch (JSONException ex) {

                }
                Campos_array[i] = new Campo(obj.getLong("id"), obj.getString("caption"), Tipo_paso.values()[obj.getInt("field_type")],values);
            }
            JSONArray JSON_DECISION_ARRAY = Contenido_json.getJSONArray("Decisions");
            Decision Decisions[] = new Decision[JSON_DECISION_ARRAY.length()];
            for (int i = 0; i < JSON_DECISION_ARRAY.length(); i++) {
                JSONObject OBJETO_JSON = JSON_DECISION_ARRAY.getJSONObject(i);
                JSONArray Vector_Ramas = OBJETO_JSON.getJSONArray("branch");
                Branch Ramas_Usadas[] = new Branch[Vector_Ramas.length()];
                for (int j = 0; j< Vector_Ramas.length(); j++) {
                    JSONObject obj1 = Vector_Ramas.getJSONObject(j);
                    Ramas_Usadas[j] = new Branch(obj1.getLong("field_id"), getType(obj1.getString("comparison_type")), obj1.getString("value"));
                }
                Decisions[i] = new Decision(Ramas_Usadas, OBJETO_JSON.getLong("go_to_step"));
            }
            step.Contenido = new Contenido(Campos_array, Decisions);
            int i=0;
        } catch (JSONException e) {
            Log.e(e.toString(), e.getMessage());
        }
        return step;
    }

    public static Tipo_comparacion getType(String data) {
        if (data.equals("="))
            return Tipo_comparacion.Igual;
        if (data.equals("<"))
            return Tipo_comparacion.Menor_Que;
        return Tipo_comparacion.Mayor_Que;
    }

    public static View QuestionType(Context c, Campo Campo, final Frag4Step frag) {
        View v = View.inflate(c, R.layout.question_step, null);
        TextView tv = (TextView) v.findViewById(R.id.question_caption);
        tv.setText(Campo.titulo);
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> SpinnerFiller = new ArrayAdapter<CharSequence>(c, R.layout.list_item, Campo.VPosibleCampo);
        SpinnerFiller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(SpinnerFiller);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                frag.Ir_a_sig_paso();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                frag.Ir_a_sig_paso();
            }
        });
        return v;
    }
    public static View BooleanType(Context c, Campo Campo, final Frag4Step frag) {
        View v = View.inflate(c, R.layout.boolean_step,null);
        TextView Titulo_Paso = (TextView) v.findViewById(R.id.bool_title);
        Titulo_Paso.setText(Campo.titulo);
        CheckBox check = (CheckBox) v.findViewById(R.id.checkBox);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                frag.Ir_a_sig_paso();
            }
        });
        return v;
    }
    public static View NumericType(Context c, Campo Campo, final Frag4Step frag) {
        View v = View.inflate(c, R.layout.numeric_step, null);
        TextView Titulo_Paso = (TextView) v.findViewById(R.id.num_title);
        Titulo_Paso.setText(Campo.titulo);
        EditText tv_num = (EditText) v.findViewById(R.id.num_text);
        tv_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {frag.Ir_a_sig_paso();}
            @Override
            public void afterTextChanged(Editable s) {frag.Ir_a_sig_paso();
            }
        });

        return v;
    }
    public static View TextType(Context c, Campo Campo, final Frag4Step frag) {
        View v = View.inflate(c, R.layout.text_step, null);
        TextView tv = (TextView) v.findViewById(R.id.text_title);
        tv.setText(Campo.titulo);
        return v;
    }
}

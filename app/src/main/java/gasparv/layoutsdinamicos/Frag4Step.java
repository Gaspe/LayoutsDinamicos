package gasparv.layoutsdinamicos;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.HashMap;

/**
 * Created by GasparV on 31/05/2015.
 */
public class Frag4Step extends Fragment {
    public Class4step step;
    private Activity4Step activity;
    private HashMap<Long, View> Contenedor_Vistas;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View Vista_agregada = inflater.inflate(R.layout.fragment_steps_, container, false);
        AgregarViews(Vista_agregada);
        return Vista_agregada;
    }

    private void AgregarViews(View Vista_Usada) {
        LinearLayout LayoutUsado = (LinearLayout)Vista_Usada;
        ((LinearLayout) Vista_Usada).setOrientation(LinearLayout.VERTICAL);
        Contenedor_Vistas = new HashMap<>();
        View Añadido = null;
        for (Class4step.Campo CampoUsado : step.Contenido.campos) {
            if(CampoUsado.tipo_campo.equals(Class4step.Tipo_paso.Pregunta))
            {   Añadido = Class4step.QuestionType(this.getActivity(), CampoUsado, this); Contenedor_Vistas.put(CampoUsado.Campo_id, Añadido);LayoutUsado.addView(Añadido);}
            else
            {   if(CampoUsado.tipo_campo.equals(Class4step.Tipo_paso.Si_o_No))
                {   Añadido = Class4step.BooleanType(this.getActivity(), CampoUsado, this);Contenedor_Vistas.put(CampoUsado.Campo_id, Añadido);LayoutUsado.addView(Añadido);}
                else
                {   if(CampoUsado.tipo_campo.equals(Class4step.Tipo_paso.Numerico))
                    {   Añadido = Class4step.NumericType(this.getActivity(), CampoUsado, this);Contenedor_Vistas.put(CampoUsado.Campo_id, Añadido);LayoutUsado.addView(Añadido);}
                    else
                    {   if(CampoUsado.tipo_campo.equals(Class4step.Tipo_paso.Texto))
                        {   Añadido = Class4step.TextType(this.getActivity(), CampoUsado, this);Contenedor_Vistas.put(CampoUsado.Campo_id, Añadido);LayoutUsado.addView(Añadido);}
                    }
                }
            }

        }
        Ir_a_sig_paso();
    }
    public void Ir_a_sig_paso() {
        for (Class4step.Decision Decision_Actual : step.Contenido.Decisiones) {
            boolean Hay_sig_paso = true;
            for (Class4step.Branch RamaUsada : Decision_Actual.Branches) {
                Class4step.Campo field = null;
                for (Class4step.Campo f : step.Contenido.campos) {if (f.Campo_id == RamaUsada.Campo_id) {field = f;break;}}
                if (field != null)
                {
                    switch (field.tipo_campo) {
                        case Pregunta:
                            Spinner sp = (Spinner)((ViewGroup)Contenedor_Vistas.get(field.Campo_id)).getChildAt(1);
                            if (!RamaUsada.Value.equals(sp.getSelectedItem().toString()))
                                Hay_sig_paso = false;
                            break;
                        case Si_o_No:
                            CheckBox box = (CheckBox)((ViewGroup)Contenedor_Vistas.get(field.Campo_id)).getChildAt(1);
                            if (!(box.isChecked() && RamaUsada.Value.equals("True")))
                                if (!(!box.isChecked() && RamaUsada.Value.equals("False")))
                                    Hay_sig_paso = false;
                            break;
                        case Numerico:
                            EditText et = (EditText)((ViewGroup)Contenedor_Vistas.get(field.Campo_id)).getChildAt(1);
                            if (et.getText().length() > 0) {
                                int val = Integer.parseInt(et.getText().toString());
                                int val_comp = Integer.parseInt(RamaUsada.Value);
                                switch (RamaUsada.tcomparacion) {
                                    case Mayor_Que:
                                        if (!(val_comp < val))
                                            Hay_sig_paso = false;
                                        break;
                                    case Menor_Que:
                                        if (!(val_comp > val))
                                            Hay_sig_paso = false;
                                        break;
                                    case Igual:
                                        if (!(val_comp == val))
                                            Hay_sig_paso = false;
                                        break;
                                }
                            } else {
                                Hay_sig_paso = false;
                            }
                            break;
                    }
                }
                else
                {Hay_sig_paso = false;}
                if (!Hay_sig_paso)
                    break;
            }
            if (Hay_sig_paso) {
                activity.sig_paso =Decision_Actual.ir_a_paso;
                int i= 0;
                break;
            } else {

            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = (Activity4Step)activity;
        super.onAttach(activity);
    }
}

package res.jonathansuarez.aplicacio0;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class AdaptadorPuntuacions extends RecyclerView.Adapter<AdaptadorPuntuacions.ViewHolder> {
    private LayoutInflater inflador; //Crea layout a partir del xml
    private Vector<String> llista; //Llista de puntuacions
    private int cont=0;
    private Context context;
    protected View.OnClickListener onClickListener;

    public AdaptadorPuntuacions(Context context, Vector<String> llista) {
        inflador = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.llista = llista;
        this.context = context;
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titol, subtitol;
        public ImageView icono;

        public ViewHolder(View itemView) {
            super(itemView);
            titol = itemView.findViewById(R.id.titol);
            subtitol = itemView.findViewById(R.id.subtitol);
            icono = itemView.findViewById(R.id.icono);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.element_puntuacio, parent, false);
        v.setOnClickListener(onClickListener);
        //Toast.makeText(context, "Cont "+(++cont), Toast.LENGTH_SHORT).show();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titol.setText(llista.get(position));
        //Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show();
        switch (Math.round((float)Math.random()*3)) {
            case 0:
                holder.icono.setImageResource(R.drawable.asteroide1);
                break;
            case 1:
                holder.icono.setImageResource(R.drawable.asteroide2);
                break;
            case 2:
                holder.icono.setImageResource(R.drawable.asteroide3);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return llista.size();
    }
}

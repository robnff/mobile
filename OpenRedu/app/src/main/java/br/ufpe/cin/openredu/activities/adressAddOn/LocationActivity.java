package br.ufpe.cin.openredu.activities.adressAddOn;

        import android.annotation.TargetApi;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.Build;
        import android.support.v4.app.NavUtils;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

public class LocationActivity extends AppCompatActivity {

    /*
    Essa activity recebe os dados enviados da SearchLocationActivity e recebe mais alguns dados
    adicionais antes de enviar por email um update para os alunos inscritos naquele desafio
    especifico. A ideia e que os dados adicionais possam explicar aos alunos por que essa localidade
    e importante para o desafio, o que eles devem procurar la que acrescentaria a eles ou qualquer
    informacao adicional que o professor queira passar.

    Vale ressaltar que o uso do email para guardar essas informacoes foi uma escolha a nivel de
    prototipo, que embora sua utilidade possa ser validada depois, sugerimos a integracao futura com
     o banco de dados do OpenREDU para manter todas as informacoes centralizadas no app
     */

    private EditText tituloDesafioET, descET, emailET, enderecoET;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Intent intent = getIntent();
        final String address = intent.getStringExtra(SearchLocationActivity.EXTRA_ADDRESS);
        String city = intent.getStringExtra(SearchLocationActivity.EXTRA_CITY);
        String state = intent.getStringExtra(SearchLocationActivity.EXTRA_STATE);
        String postalCode = intent.getStringExtra(SearchLocationActivity.EXTRA_POSTAL_CODE);

        /*
        os dados recebidos do intent servem para garantir ao usuario que ele possa ver o endereco
        que escolheu e, caso veja algum erro, retornar para escolher o correto.
         */
        enderecoET.setText(address + ", " + city + ", " + state + " - " + postalCode);

        Button button = (Button) findViewById(R.id.postarDesafio);
        tituloDesafioET = (EditText) findViewById(R.id.TituloDesafio);
        descET = (EditText) findViewById(R.id.DescDesafio);
        emailET = (EditText) findViewById(R.id.Email);
        enderecoET = (EditText) findViewById(R.id.Address);

        /*
        ao clicar o botao, esse metodo e o responsavel por enviar o email, ele seleciona o que foi
        digitado nos campos, separa os emails por virgula e abre o aplicativo de email do usuario
        para finalizar o envio. O intuito e que a descricao contenha as informacoes adicionais
        descritas acima.
         */
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String titulo = tituloDesafioET.getText().toString();
                String desc = descET.getText().toString();
                String[] emails = emailET.getText().toString().split(",");
                String endereco = enderecoET.getText().toString();

                Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
                // prompts email clients only
                email.setType("message/rfc822");
                email.putExtra(Intent.EXTRA_EMAIL, emails);
                email.putExtra(Intent.EXTRA_SUBJECT, "[desafio OpenREDU] " + titulo);
                email.putExtra(Intent.EXTRA_TEXT, desc + "\nEndereço: " + endereco);

                try {
                    // the user can choose the email client
                    startActivity(Intent.createChooser(email, "Choose an email client from..."));

                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(LocationActivity.this, "No email client installed.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
